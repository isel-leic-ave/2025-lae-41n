

public class FooJava {
    public final static FooJava Instance;

    static {
        Instance = new FooJava();
    }

    private FooJava() {
    }
    public void m1() {}
    //public static FooJava getInstance() { return Instance; }
}

class App {
    public static void main(String[] args) {
        FooJava.Instance.m1();
    }
}
