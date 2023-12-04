package backend;

public class PhysicalReg {

    public Name name;
    public PhysicalReg(Name name) {
        this.name = name;
    }

    public static final PhysicalReg SP = new PhysicalReg(Name.SP);
    public static final PhysicalReg RA = new PhysicalReg(Name.RA);
    public static final PhysicalReg V0 = new PhysicalReg(Name.V0);


    public enum Name {
        V0("$v0"),
        T1("$t1"), T2("$t2"), T3("$t3"), T4("$t4"), T5("$t5"), T6("$t6"), T7("$t7"), T8("$t8"), T9("$t9"),
        S1("$s1"), S2("$s2"), S3("$s3"), S4("$s4"), S5("$s5"), S6("$s6"), S7("$s7"),
        RA("$ra"),
        SP("$sp"),
        ;
        final String name;
        private Name(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
