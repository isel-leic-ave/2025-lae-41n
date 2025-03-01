import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        try {


//            Person p = new Person(1, "Tomás Araújo");
//            Person p2 = new Person(2, "Otamendi");
//
//            System.out.println(p.getBorn());
//            System.out.println(p2.getBorn());
//
//
            Class<Person> personClass = Person.class;

            System.out.println("##### Person fields");
            Stream.of(personClass.getDeclaredFields()).forEach(
                    field -> {
                        System.out.println(field.getName());
                    });

            System.out.println("##### Person methods");
            Stream.of(personClass.getDeclaredMethods()).forEach(
                    method -> {
                        System.out.println(method.getName());
                    });

//
//            Field bornField = personClass.getDeclaredField("born");
//            System.out.println(bornField.getName().toString());
//            System.out.println(bornField.getType().toString());
//            int modifiers = bornField.getModifiers();
//            System.out.println(Modifier.toString(modifiers));
//            //System.out.println(bornField.getModifiers());
//
//            String slb = "Benfica";
//            bornField.setAccessible(true);
//            bornField.set(p, 222222222222L);
//
//            System.out.println(p.getBorn());
//            System.out.println(p2.getBorn());
//
//            System.out.println(bornField.get(p));
//            System.out.println(bornField.get(p2));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


