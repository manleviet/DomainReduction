import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.util.Locale;

public class CarModel {

    public static String[] varNames = new String[] {"modell", "farbe", "motorisierung", "anwendung", "preisgruppe", "antriebsart"};
    public static IntVar[] vars;
    public static Model model;

    public static void createVariables() {
        // 1 - limousine , 2 - combi , 3 - suv, 4 - cabrio, 5 - van
        IntVar modell = model.intVar(varNames[0], new int[]{1, 2, 3, 4, 5});
        // 1 - schwarz , 2 - weib , 3 - grau, 4 - blau, 5 - rot
        IntVar farbe = model.intVar(varNames[1], new int[]{1, 2, 3, 4, 5});
        IntVar motorisierung = model.intVar(varNames[2], new int[]{100, 140, 180, 220, 260});
        // Other variables
        // 0 - pkw, 1 - transporter
        IntVar anwendung = model.intVar(varNames[3], new int[]{0, 1});
        // 0 - standard, 1 - preisklasse1, 2 - preisklasse2
        IntVar preisgruppe = model.intVar(varNames[4], new int[]{0, 1, 2});
        // 0 - benzin, 1 - diesel, 2 - elektrisch
        IntVar antriebsart = model.intVar(varNames[5], new int[]{0, 1, 2});

        vars = new IntVar[] {modell, farbe, motorisierung, anwendung, preisgruppe, antriebsart};
    }

    public static void createKB() {
        // Constraints from the tables
        // Constraint c1: modell = limousine => anwendung = pkw
        // + using ifThen method to encode the imply operator. The
        //   ifThen method will be automatically posted.
        // + using arithm method to express the arithmetical constraints
        model.ifThen(
                model.arithm(vars[0],"=",1),
                model.arithm(vars[3],"=",0)
        );

        // Constraint c2: modell = combi => anwendung = transporter
        model.ifThen(
                model.arithm(vars[0],"=",2),
                model.arithm(vars[3],"=",1)
        );

        // Constraint c3: modell = suv => anwendung = pkw
        model.ifThen(
                model.arithm(vars[0],"=",3),
                model.arithm(vars[3],"=",0)
        );

        // Constraint c4: modell = cabrio => anwendung = pkw
        model.ifThen(
                model.arithm(vars[0],"=",4),
                model.arithm(vars[3],"=",0)
        );

        // Constraint c5: modell = van => anwendung = transporter
        model.ifThen(
                model.arithm(vars[0],"=",5),
                model.arithm(vars[3],"=",1)
        );

        // Constraint c6: farbe = schwarz => preisgruppe = standard
        model.ifThen(
                model.arithm(vars[1],"=",1),
                model.arithm(vars[4],"=",0)
        );

        // Constraint c7: farbe = weib => preisgruppe = preisklasse1
        model.ifThen(
                model.arithm(vars[1],"=",2),
                model.arithm(vars[4],"=",1)
        );

        // Constraint c8: farbe = grau => preisgruppe = preisklasse1
        model.ifThen(
                model.arithm(vars[1],"=",3),
                model.arithm(vars[4],"=",1)
        );

        // Constraint c9: farbe = blau => preisgruppe = preisklasse2
        model.ifThen(
                model.arithm(vars[1],"=",4),
                model.arithm(vars[4],"=",2)
        );

        // Constraint c10: farbe = rot => preisgruppe = preisklasse2
        model.ifThen(
                model.arithm(vars[1],"=",5),
                model.arithm(vars[4],"=",2)
        );

        // Constraint c11: motorisierung = 100 => antriebsart = benzin
        model.ifThen(
                model.arithm(vars[2],"=",100),
                model.arithm(vars[5],"=",0)
        );

        // Constraint c12: motorisierung = 140 => antriebsart = diesel
        model.ifThen(
                model.arithm(vars[2],"=",140),
                model.arithm(vars[5],"=",1)
        );

        // Constraint c13: motorisierung = 180 => antriebsart = diesel
        model.ifThen(
                model.arithm(vars[2],"=",180),
                model.arithm(vars[5],"=",1)
        );

        // Constraint c14: motorisierung = 220 => antriebsart = benzin
        model.ifThen(
                model.arithm(vars[2],"=",220),
                model.arithm(vars[5],"=",0)
        );

        // Constraint c15: motorisierung = 260 => antriebsart = elektrisch
        model.ifThen(
                model.arithm(vars[2],"=",260),
                model.arithm(vars[5],"=",2)
        );

        // Restrictions
        // Constraint c16: Diesel Limousine does not come in blue and gray
        // modell = limousine /\ antriebsart = diesel => farbe != blau /\ farbe != grau
        model.ifThen(
                model.and(model.arithm(vars[0],"=",1),
                        model.arithm(vars[5],"=",1)),
                model.and(model.arithm(vars[1],"!=",3),
                        model.arithm(vars[1],"!=",4))
        );
        // Constraint c17: Benzin Limousine does not exist in price class1
        // modell = limousine /\ antriebsart = benzin => preisgruppe != preisklasse1
        model.ifThen(
                model.and(model.arithm(vars[0],"=",1),
                        model.arithm(vars[5],"=",0)),
                model.arithm(vars[4],"!=",1)
        );
        // Constraint c18: Transporter is only available in electric or diesel version
        // anwendung = transporter => antriebsart = elektrisch \/ antriebsart = diesel
        // anwendung = transporter => antriebsart != benzin
        model.ifThen(
                model.arithm(vars[3],"=",1),
                model.arithm(vars[5],"!=",0)
        );
        // Constraint c19: Cabrios are not available in Standard colors and only as Diesel and Benzin models.
        // modell = cabrio => preisgruppe != standard
        model.ifThen(
                model.arithm(vars[0],"=",4),
                model.arithm(vars[4],"!=",0)
        );
        // Constraint c20: But the Red Carbio is also available electrically
        // modell = cabrio /\ farbe = rot => antriebsart = elektrisch
        model.ifThen(
                model.and(model.arithm(vars[0],"=",4),
                        model.arithm(vars[1],"=",5)),
                model.arithm(vars[5],"=",2)
        );
        // modell = cabrio /\ farbe != rot => antriebsart != elektrisch
        model.ifThen(
                model.and(model.arithm(vars[0],"=",4),
                        model.arithm(vars[1],"!=",5)),
                model.arithm(vars[5],"!=",2)
        );
    }

    public static void main(String[] args) {
        model = new Model("Combeenation Car Model");

        // Decision variables
        createVariables();

        // Knowledge Base
        createKB();

        // Variables' domain before applying restrictions
        System.out.println("------ INITIAL DOMAINS ------");
        printAllVarDomains();

        // CHECK THE FIRST RESTRICTION & THE SECOND RESTRICTION
        // Diesel-Limousine gibt's nicht in Blau und Grau
        // Benzin-Limousine gibt's nicht in Preisklasse1

        // first selection: Limousine
        // second selection: 140 kW (diesel)
//        scenario1(); // not blau, not grau

        // first selection: Limousine
        // second selection: 220 kW (benzin)
//        scenario2(); // not class1 -> not weib, not grau

        // first selection: Limousine
        // second selection: blau (class 2)
//        scenario3(); // not diesel

        // first selection: Limousine
        // second selection: grau (class 1)
        scenario4(); // not diesel, not benzin

        // CHECK THE THIRD RESTRICTION
        // Transporter gibt's nur Elektrisch oder Diesel

        // first selection: Combi --> no Benzin
        // second selection: Grau
//        scenario5();

        // first selection: Van --> no Benzin
        // second selection: 260 kW
//        scenario6();

        // CHECK THE FOURTH RESTRICTION & THE FIFTH RESTRICTION
        // Cabrios gibt's nicht in Standard-Farben und nur als Diesel und Benziner
        // Das rote Carbio gibt's aber auch elektrisch

        // first selection: Cabrio --> not Schwarz
        // second selection: Diesel or Benzin
//        scenario7();

        // first selection: Cabrio --> not Schwarz
        // second selection: Elekstrisch
//        scenario8();

        // first selection: Cabrio --> not Schwarz
        // second selection: Rot
//        scenario9();

        // first selection: Cabrio --> not Schwarz
        // second selection: Weis
//        scenario10();
    }

    private static void scenario10() {
        // First user selection
        model.arithm(vars[0],"=",4).post();

        System.out.println();
        System.out.println("------ First, User selects CABRIO ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[1],"=",2).post();

        System.out.println();
        System.out.println("------ Second, User selects WEIB ------");
        propagate(model);
    }

    private static void scenario9() {
        // First user selection
        model.arithm(vars[0],"=",4).post();

        System.out.println();
        System.out.println("------ First, User selects CABRIO ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[1],"=",5).post();

        System.out.println();
        System.out.println("------ Second, User selects ROT ------");
        propagate(model);
    }

    private static void scenario8() {
        // First user selection
        model.arithm(vars[0],"=",4).post();

        System.out.println();
        System.out.println("------ First, User selects CABRIO ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[2],"=",260).post();

        System.out.println();
        System.out.println("------ Second, User selects 260 kW motor (Elektrisch) ------");
        propagate(model);
    }

    private static void scenario7() {
        // First user selection
        model.arithm(vars[0],"=",4).post();

        System.out.println();
        System.out.println("------ First, User selects CABRIO ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[2],"=",100).post();

        System.out.println();
        System.out.println("------ Second, User selects 100 kW motor (Benzin) ------");
        propagate(model);
    }

    private static void scenario6() {
        // First user selection
        model.arithm(vars[0],"=",5).post();

        System.out.println();
        System.out.println("------ First, User selects VAN ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[2],"=",260).post();

        System.out.println();
        System.out.println("------ Second, User selects 260 kW motor (Elektrisch) ------");
        propagate(model);
    }

    private static void scenario5() {
        // First user selection
        model.arithm(vars[0],"=",2).post();

        System.out.println();
        System.out.println("------ First, User selects COMBI ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[1],"=",3).post();

        System.out.println();
        System.out.println("------ Second, User selects GRAU ------");
        propagate(model);
    }

    private static void scenario4() {
        // First user selection
        model.arithm(vars[0],"=",1).post();

        System.out.println();
        System.out.println("------ First, User selects LIMOUSINE ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[1],"=",3).post();

        System.out.println();
        System.out.println("------ Second, User selects GRAU ------");
        propagate(model);
    }

    private static void scenario3() {
        // First user selection
        model.arithm(vars[0],"=",1).post();

        System.out.println();
        System.out.println("------ First, User selects LIMOUSINE ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[1],"=",4).post();

        System.out.println();
        System.out.println("------ Second, User selects BLAU ------");
        propagate(model);
    }

    private static void scenario2() {
        // First user selection
        model.arithm(vars[0],"=",1).post();

        System.out.println();
        System.out.println("------ First, User selects LIMOUSINE ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[2],"=",220).post();

        System.out.println();
        System.out.println("------ Second, User selects 220 kW motor (Benzin) ------");
        propagate(model);
    }

    private static void scenario1() {
        // First user selection
        model.arithm(vars[0],"=",1).post();

        System.out.println();
        System.out.println("------ First, User selects LIMOUSINE ------");
        propagate(model);

        // Second user selection
        model.arithm(vars[2],"=",140).post();

        System.out.println();
        System.out.println("------ Second, User selects 140 kW motor (Diesel) ------");
        propagate(model);
    }

    public static void propagate(Model model) {
        // get the Solver object from the Model object
        Solver solver = model.getSolver();
        // save the original model
        model.getEnvironment().worldPush();
        try {
            solver.propagate(); // propagate

            printAllVarDomains(); // print variable domains
        } catch (ContradictionException ex) { // in case of a contradiction

            solver.getEngine().flush();
            System.out.println("There is a contradiction.");
            printAllVarDomains(); // print variable domains
        }
        // get back the original model
        model.getEnvironment().worldPop();
        model.getSolver().reset();
    }

    public static void printAllVarDomains() {
        for (IntVar var: vars) {
            printVarDomain(var);
        }
    }

    public static void printVarDomain(IntVar var) {
        DisposableValueIterator vit = var.getValueIterator(true);
        System.out.print(var.getName().toUpperCase(Locale.ROOT) + "'s domain: ");
        boolean firstValue = true;
        while (vit.hasNext()) {
            if (!firstValue) {
                System.out.print(", ");
            } else {
                firstValue = false;
            }
//            System.out.print(vit.next());
            System.out.print(getRealValue(var.getName(), vit.next()));
        }
        System.out.println();
        vit.dispose();
    }

    public static String getRealValue(String varName, int value) {
        switch (varName) {
            case "modell":
                return getRealValueForModell(value);
            case "farbe":
                return getRealValueForFarbe(value);
            case "motorisierung":
                return getRealValueForMotorisierung(value);
            case "anwendung":
                return getRealValueForAnwendung(value);
            case "preisgruppe":
                return getRealValueForPreisgruppe(value);
            case "antriebsart":
                return getRealValueForAntriebsart(value);
            default:
                return "";
        }
    }

    private static String getRealValueForAntriebsart(int value) {
        // 0 - benzin, 1 - diesel, 2 - elektrisch
        switch (value) {
            case 0:
                return "benzin";
            case 1:
                return "diesel";
            case 2:
                return "elektrisch";
            default:
                return "";
        }
    }

    private static String getRealValueForPreisgruppe(int value) {
        // 0 - standard, 1 - preisklasse1, 2 - preisklasse2
        switch (value) {
            case 0:
                return "standard";
            case 1:
                return "preisklasse1";
            case 2:
                return "preisklasse2";
            default:
                return "";
        }
    }

    private static String getRealValueForAnwendung(int value) {
        // 0 - pkw, 1 - transporter
        switch (value) {
            case 0:
                return "pkw";
            case 1:
                return "transporter";
            default:
                return "";
        }
    }

    public static String getRealValueForModell(int value) {
        // 1 - limousine , 2 - combi , 3 - suv, 4 - cabrio, 5 - van
        switch (value) {
            case 1:
                return "limousine";
            case 2:
                return "combi";
            case 3:
                return "suv";
            case 4:
                return "cabrio";
            case 5:
                return "van";
            default:
                return "";
        }
    }

    public static String getRealValueForFarbe(int value) {
        // 1 - schwarz , 2 - weib , 3 - grau, 4 - blau, 5 - rot
        switch (value) {
            case 1:
                return "schwarz";
            case 2:
                return "weib";
            case 3:
                return "grau";
            case 4:
                return "blau";
            case 5:
                return "rot";
            default:
                return "";
        }
    }

    public static String getRealValueForMotorisierung(int value) {
        // 100, 140, 180, 220, 260
        return "" + value + " kW";
    }
}