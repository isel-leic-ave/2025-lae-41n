class Person extends Object {
        public int id;
        public String name = "Pavlidis";
        private long born;

        public Person(int id, String name) {
                name = "Pavlidis";
                this.id = id;
                this.name = name;
        }

        public int getId() {
                return this.id;
        }

        public String getName() {
                return name;
        }

        public long getBorn() {
                return born;
        }
}

