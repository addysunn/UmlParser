package parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class data{
	
           	public String classname = new String();
        	public ArrayList extended=new ArrayList();
            public ArrayList implemented=new ArrayList();
            public boolean isInter;
            
            public fields fld= new fields();
            public methods mthd= new methods();
            public String constructor="";
            public ArrayList cparams=new ArrayList();
            
}