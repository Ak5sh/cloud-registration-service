package com.university;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(8080);

        get("/", (req, res) -> {
            res.type("text/html");
            return "<html><body style='font-family:sans-serif;text-align:center;padding:50px;'>"
                 + "<h1 style='color:#1B365D;'>Cloud Registration Service</h1>"
                 + "<p>Status: <strong>Active & Healthy</strong></p>"
                 + "<p>Deployment: Automated CI/CD Pipeline v2.0.0 (Live Redeploy)</p>"
                 + "</body></html>";
        });

        get("/health", (req, res) -> "OK");
    }

    public static boolean checkRegistration(int currentCourses) {
        return currentCourses < 5;
    }
}
