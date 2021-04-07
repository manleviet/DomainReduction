import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

public class CarModel {

    public static IntVar[] vars;

    public static void main(String[] args) {
        Model model = new Model("Combeenation Car Model");

        // Decision variables
        // 1 - limousine , 2 - combi , 3 - suv, 4 - cabrio, 5 - van
        IntVar modell = model.intVar("modell", new int[]{1,2,3,4,5});
        // 1 - schwarz , 2 - weib , 3 - grau, 4 - blau, 5 - rot
        IntVar farbe = model.intVar("farbe", new int[]{1,2,3,4,5});
        IntVar motorisierung = model.intVar("motorisierung", new int[]{100,140,180,220,260});
        // Other variables
        // 0 - pkw, 1 - transporter
        IntVar anwendung = model.intVar("anwendung", new int[]{0,1});
        // 0 - standard, 1 - preisklasse1, 2 - preisklasse2
        IntVar preisgruppe = model.intVar("preisgruppe", new int[]{0,1,2});
        // 0 - benzin, 1 - diesel, 2 - elektrisch
        IntVar antriebsart = model.intVar("antriebsart", new int[]{0,1,2});

        vars = new IntVar[] {modell, farbe, motorisierung, anwendung, preisgruppe, antriebsart};

        // Knowledge Base
        // Constraints from the tables
        // Constraint c1: modell = limousine => anwendung = pkw
        // + using ifThen method to encode the imply operator. The
        //   ifThen method will be automatically posted.
        // + using arithm method to express the arithmetical constraints
        model.ifThen(
                model.arithm(modell,"=",1),
                model.arithm(anwendung,"=",0)
        );

        // Constraint c2: modell = combi => anwendung = transporter
        model.ifThen(
                model.arithm(modell,"=",2),
                model.arithm(anwendung,"=",1)
        );

        // Constraint c3: modell = suv => anwendung = pkw
        model.ifThen(
                model.arithm(modell,"=",3),
                model.arithm(anwendung,"=",0)
        );

        // Constraint c4: modell = cabrio => anwendung = pkw
        model.ifThen(
                model.arithm(modell,"=",4),
                model.arithm(anwendung,"=",0)
        );

        // Constraint c5: modell = van => anwendung = transporter
        model.ifThen(
                model.arithm(modell,"=",5),
                model.arithm(anwendung,"=",1)
        );

        // Constraint c6: farbe = schwarz => preisgruppe = standard
        model.ifThen(
                model.arithm(farbe,"=",1),
                model.arithm(preisgruppe,"=",0)
        );

        // Constraint c7: farbe = weib => preisgruppe = preisklasse1
        model.ifThen(
                model.arithm(farbe,"=",2),
                model.arithm(preisgruppe,"=",1)
        );

        // Constraint c8: farbe = grau => preisgruppe = preisklasse1
        model.ifThen(
                model.arithm(farbe,"=",3),
                model.arithm(preisgruppe,"=",1)
        );

        // Constraint c9: farbe = blau => preisgruppe = preisklasse2
        model.ifThen(
                model.arithm(farbe,"=",4),
                model.arithm(preisgruppe,"=",2)
        );

        // Constraint c10: farbe = rot => preisgruppe = preisklasse2
        model.ifThen(
                model.arithm(farbe,"=",5),
                model.arithm(preisgruppe,"=",2)
        );

        // Constraint c11: motorisierung = 100 => antriebsart = benzin
        model.ifThen(
                model.arithm(motorisierung,"=",100),
                model.arithm(antriebsart,"=",0)
        );

        // Constraint c12: motorisierung = 140 => antriebsart = diesel
        model.ifThen(
                model.arithm(motorisierung,"=",140),
                model.arithm(antriebsart,"=",1)
        );

        // Constraint c13: motorisierung = 180 => antriebsart = diesel
        model.ifThen(
                model.arithm(motorisierung,"=",180),
                model.arithm(antriebsart,"=",1)
        );

        // Constraint c14: motorisierung = 220 => antriebsart = benzin
        model.ifThen(
                model.arithm(motorisierung,"=",220),
                model.arithm(antriebsart,"=",0)
        );

        // Constraint c15: motorisierung = 260 => antriebsart = elektrisch
        model.ifThen(
                model.arithm(motorisierung,"=",260),
                model.arithm(antriebsart,"=",2)
        );

        // Restrictions
        // Constraint c16: Diesel-Limousine gibt's nicht in Blau und Grau
        // modell = limousine /\ antriebsart = diesel => not(farbe = blau \/ farbe = grau)
        model.ifThen(
                model.and(model.arithm(modell,"=",1),
                        model.arithm(antriebsart,"=",1)),
                model.and(model.arithm(farbe,"!=",3),
                        model.arithm(farbe,"!=",4))
        );
        // Constraint c17: Benzin-Limousine gibt's nicht in Preisklasse1
        // modell = limousine /\ antriebsart = benzin => not(preisgruppe = preisklasse1)
        model.ifThen(
                model.and(model.arithm(modell,"=",1),
                        model.arithm(antriebsart,"=",0)),
                model.arithm(preisgruppe,"!=",1)
        );
        // Constraint c18: Transporter gibt's nur Elektrisch oder Diesel
        // anwendung = transporter => antriebsart != elektrisch /\ antriebsart != diesel
        model.ifThen(
                model.arithm(anwendung,"=",1),
                model.and(model.arithm(antriebsart,"!=",2),
                        model.arithm(antriebsart,"!=",1))
        );
        // Constraint c19: Cabrios gibt's nicht in Standard-Farben und nur als Diesel und Benziner
        // modell = cabrio => preisgruppe != standard /\ antriebsart != diesel /\ antriebsart != benzin
        model.ifThen(
                model.arithm(modell,"=",4),
                model.and(model.arithm(preisgruppe,"!=",0),
                          model.arithm(antriebsart,"!=",0),
                          model.arithm(antriebsart,"!=",1))
        );
        // Constraint c20: Das rote Carbio gibt's aber auch elektrisch
        // modell = cabrio /\ farbe = rot => antriebsart = elektrisch
        model.ifThen(
                model.and(model.arithm(modell,"=",4),
                          model.arithm(farbe,"=",5)),
                model.arithm(antriebsart,"=",2)
        );

        // Variables' domain before applying restrictions
        System.out.println("Before the domain reduction");
        printAllVarDomains();

        // First user selection
        model.arithm(modell,"=",1).post();

        System.out.println();
        System.out.println("After the first domain reduction when user selects limousine");
        propagate(model);

        // Second user selection
        model.arithm(motorisierung,"=",140).post();

        System.out.println();
        System.out.println("After the second domain reduction when user selects 140 kW motor");
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
        System.out.print(var.getName() + "'s domain: ");
        while (vit.hasNext()) {
            System.out.print(vit.next() + " ");
        }
        System.out.println();
        vit.dispose();
    }
}