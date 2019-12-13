import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RServeTest {

    public static void main(String[] args) throws RserveException {

        RConnection rConnection = new RConnection("localhost", 2587);
        rConnection.eval("Cds < c(1.5,2.5)");

        rConnection.close();
        rConnection.shutdown();
        rConnection.serverShutdown();
        rConnection.voidEval("q()");
        rConnection.voidEvalDetach("q()");
    }

}
