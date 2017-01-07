/**
 * Created by jks on 1/6/17.
 */
import static spark.Spark.*;

public class Client {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");

    }
}
