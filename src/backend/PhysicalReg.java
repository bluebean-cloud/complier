package backend;

public class PhysicalReg {

    public final Name name;
    public PhysicalReg(Name name) {
        this.name = name;
    }

    public static final PhysicalReg SP = new PhysicalReg(Name.SP);
    public static final PhysicalReg RA = new PhysicalReg(Name.RA);
    public static final PhysicalReg V0 = new PhysicalReg(Name.V0);
    public static final PhysicalReg T1 = new PhysicalReg(Name.T1);
    public static final PhysicalReg T2 = new PhysicalReg(Name.T2);
    public static final PhysicalReg T3 = new PhysicalReg(Name.T3);
    public static final PhysicalReg T4 = new PhysicalReg(Name.T4);
    public static final PhysicalReg T5 = new PhysicalReg(Name.T5);
    public static final PhysicalReg T6 = new PhysicalReg(Name.T6);
    public static final PhysicalReg T7 = new PhysicalReg(Name.T7);
    public static final PhysicalReg T8 = new PhysicalReg(Name.T8);
    public static final PhysicalReg T9 = new PhysicalReg(Name.T9);
    public static final PhysicalReg S1 = new PhysicalReg(Name.S1);
    public static final PhysicalReg S2 = new PhysicalReg(Name.S2);
    public static final PhysicalReg S3 = new PhysicalReg(Name.S3);
    public static final PhysicalReg S4 = new PhysicalReg(Name.S4);
    public static final PhysicalReg S5 = new PhysicalReg(Name.S5);
    public static final PhysicalReg S6 = new PhysicalReg(Name.S6);
    public static final PhysicalReg S7 = new PhysicalReg(Name.S7);
    public static final PhysicalReg A0 = new PhysicalReg(Name.A0);
    public static final PhysicalReg A1 = new PhysicalReg(Name.A1);
    public static final PhysicalReg A2 = new PhysicalReg(Name.A2);

    public enum Name {
        V0("$v0"),
        A0("$a0"), A1("$a1"), A2("$a2"),
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

    @Override
    public String toString() {
        return name.toString();
    }

    @Override
    public int hashCode() {
        return name.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PhysicalReg) {
            return name == ((PhysicalReg) obj).name;
        }
        return false;
    }
}
