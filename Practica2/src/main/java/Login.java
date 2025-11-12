import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.concurrent.Executor;

@WebServlet("/login")

public class Login extends HttpServer {
  @Override
    protect void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }



    private void inicioAplicacion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var contador = 1;
        if (req.getSession().getAttribute(Constantes.CONTADOR) != null) {
            contador = (Integer) req.getSession().getAttribute(Constantes.CONTADOR);
        }

        String param = req.getParameter("nombre");
        resp.getWriter().write("<html><h1>"+contador+" "+param+"</h1>" +

                "<form action='mi' method='post' style='margin-top: 30px; text-align: center;'>"+
                "<input type='text' name='nombre' id='nombre' value=''  />" +
                "<button type='submit' style='padding: 10px 20px; font-size: 16px; border: none; border-radius: 5px; background-color: #28a745; color: white; cursor: pointer;'>"+
                "Ir a mi"+
                "</button>"+
                "</form>"+

                "</html>");

        req.getSession().setAttribute(Constantes.CONTADOR, contador+1);
    }

    }
}
