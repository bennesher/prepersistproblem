## Demonstration of Problem with Micronaut's `@PrePersist` Annotation

I asked the following question on StackOverflow on 3/10/2022:

I'd like to set some values on my entities automatically when they're persisted, based on another object from my application context (in my case, the user making the change, fetched from the SecurityContext). Because the data is coming from a global singleton, I am trying to use Micronaut's `@PrePersist` annotation on a method of another managed bean, as described in the second form [here][1]. The logical place for this is as part of the `@Repository` that manages the entity type in question.

The trouble is, the changes made to the entity in this method don't appear to be getting picked up when Hibernate actually generates the transaction. After spending a while in the debugger, I found that Hibernate had already captured the field values of the entity into an array, prior to invoking the various pre-persist handlers, and does not pick up the changes. I note that Micronaut documentation for the entity events shows a field being modified in the entity method version of `@PrePersist`, but not in the managed bean version - but nothing in the narrative suggests that there is any such limitation.

I have confirmed that I can work around this by stashing a `static` reference to my singleton `SecurityContext`, and fetching the value in question from there using a `javax.persistence.PrePersist` method (note that the Micronaut version is completely ignored in this case). But I would also like to find out if there's something I'm missing that can make the Micronaut version of this work as I expect it to.

For reference, I am using the following versions:
* Micronaut: 3.2.2
* Hibernate: 5.5.9.Final

Here are the main classes involved (edited for presentation):

```java
@Entity
public class MyData {
    private Long id;

    @DateCreated
    @Column(nullable = false, updatable = false)
    private Timestamp created;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column
    private String data;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    // Plus the usual getters & setters
}

@Singleton
public class SecurityContext {
    private final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public <T> T with(String username, Callable<T> task) throws Exception {
        String prev = currentUser.get();
        try {
            currentUser.set(username);
            return task.call();
        } finally {
            currentUser.set(prev);
        }
    }

    public String getUser() {
        return currentUser.get();
    }
}

@Repository
public abstract class MyDataRepository implements CrudRepository<MyData, Long> {

    @Inject
    private SecurityContext context;

    @PersistenceContext
    private EntityManager em;

    @PrePersist
    public void setCreator(MyData data) {
        data.setCreatedBy(context.getUser());
        System.out.printf("Created by %s\n", data.getCreatedBy());
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }
}

public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }

    @Inject
    private MyDataRepository repository;

    @Transactional
    MyData makeData(String theData) {
        MyData data = new MyData();

        data.setData(theData);

        return repository.save(data);
    }
}

@MicronautTest
class PrePersistProblemTest {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private MyDataRepository repository;

    @Inject
    private Application application;

    @Test
    void testMakeData() throws Exception {
        MyData data1 = securityContext.with("bensc",
                () -> {
                    MyData myData = application.makeData("Hello, World!");
                    repository.flushAndClear();
                    return myData;
                });

        MyData data2 = repository.findById(data1.getId()).orElseThrow();

        assertEquals(data1.getData(), data2.getData());
        assertNotNull(data2.getCreated());
        assertEquals("bensc", data2.getCreatedBy());
    }
}
```

When I run this test, I get:

```
Created by bensc

expected: <bensc> but was: <null>
Expected :bensc
Actual   :null
```

The full source for this test, along with the workaround, is available [on GitHub][2].

[1]: https://micronaut-projects.github.io/micronaut-data/latest/guide/#entityEvents
[2]: https://github.com/bennesher/prepersistproblem