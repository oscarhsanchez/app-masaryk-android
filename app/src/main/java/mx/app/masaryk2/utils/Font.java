package mx.app.masaryk2.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class Font {
	
	static private HashMap<String,Typeface> fonts;
	
	static public Typeface get(Context c, String font) {
		
		if (fonts == null) {
			fonts = new HashMap<String,Typeface>();
			fonts.put(font, Typeface.createFromAsset(c.getAssets(), "fonts/font.ttf"));
		}
		return fonts.get(font);
	}	
	
}
