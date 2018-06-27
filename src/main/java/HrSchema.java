public class HrSchema {
    public final Employee[] emps = new Employee[1];
    public final Department[] depts = new Department[1];

    public HrSchema() {
        emps[0] = new Employee(1, "Georgios");
    }
}