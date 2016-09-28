package util.html.tags;

import org.jsoup.nodes.Element;

/**
 * Created by jpcmarques on 27-09-2016.
 */
public class TableRowHD {
    private String th, td;

    public TableRowHD(Element source){
        th = source.getElementById("th").text();
        td = source.getElementById("td").text();
    }

    public String getTh() {
        return th;
    }

    public String getTd() {
        return td;
    }
}
