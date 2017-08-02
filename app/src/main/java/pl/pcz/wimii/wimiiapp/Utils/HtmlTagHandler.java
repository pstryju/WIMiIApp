package pl.pcz.wimii.wimiiapp.Utils;

import android.text.Editable;
import android.text.Html;
import android.util.Log;

import org.xml.sax.XMLReader;

/**
 * Created by Paweł on 25.10.2016.
 */

public class HtmlTagHandler implements Html.TagHandler {
    boolean first= true;
    String parent=null;
    int index=1;
    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        if(tag.equals("ul")) parent="ul";
        else if(tag.equals("ol")) parent="ol";
        if(tag.equals("li")){
            if(parent.equals("ul")){
                if(first){
                    output.append("\n  • ");
                    first= false;
                }else{
                    first = true;
                }
            }
            else{
                if(first){
                    output.append("\n\t"+index+". ");
                    first= false;
                    index++;
                }else{
                    first = true;
                }
            }
        }

    }

}
