import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.concurrent.Executor;

@WebServlet("/login")

public class Login extends HttpServlet {

  @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();

    HttpSession session = req.getSession();
    String user = session != null? (String) session.getAttribute("user") : null;





        // estadisticas son un doget
        // esta no es un servlet su teamplate va fuera de la carpeta de teamplates
      // no son 3 servlets, son dos. la de fin es el mismo servlet que la del juego
      // uns ervelt caca que haga cosas distintas en el do gest y en el do post
      // el hecho de que tenga dos clases de constantes es una bomba, solo se usa una
}}
