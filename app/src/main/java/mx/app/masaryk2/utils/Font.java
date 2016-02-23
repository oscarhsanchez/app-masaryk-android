package mx.app.masaryk2.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class Font {
	
	static private HashMap<String,Typeface> fonts;
	
	static public Typeface get(Context c, String font) {
		
		if (fonts == null) {
			fonts = new HashMap<>();
			fonts.put("source-sans-regular",  Typeface.createFromAsset(c.getAssets(), "fonts/SourceSansPro-Regular.otf"));
			fonts.put("source-sans-light", 	  Typeface.createFromAsset(c.getAssets(), "fonts/SourceSansPro-Light.otf"));
			fonts.put("source-sans-semibold", Typeface.createFromAsset(c.getAssets(), "fonts/SourceSansPro-Semibold.otf"));
		}
		return fonts.get(font);
	}	
	
}
