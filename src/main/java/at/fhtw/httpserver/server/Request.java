package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.Method;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private HeaderMap headerMap =  new HeaderMap();
    private String body;

    public String getServiceRoute(){
        if (this.pathParts == null ||
            this.pathParts.isEmpty()) {
            return null;
        }

        String routePath = "";
        for (String part : this.pathParts){
            routePath += ('/' + part);
        }
        return routePath;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        Boolean hasParams = urlContent.indexOf("?") != -1;

        if (hasParams) {
            String[] pathParts =  urlContent.split("\\?");
            this.setPathname(pathParts[0]);
            this.setParams(pathParts[1]);
        }
        else
        {
            this.setPathname(urlContent);
            this.setParams(null);
        }
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null &&
                part.length() > 0)
            {
                this.pathParts.add(part);
            }
        }

    }

}
