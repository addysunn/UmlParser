package parser;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.SourceStringReader;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

public class uml1 {

	public static void main(String args[]) throws Exception {
		ArrayList<relations> rel = new ArrayList<relations>();
		ArrayList<String> adi = new ArrayList<String>();
		File file = new File(args[0]);
		ArrayList intrfcnms=new ArrayList();
		ArrayList uses=new ArrayList();
		ArrayList<data> d1 = new ArrayList<data>(); // data object
		String clsnms="";
		if (file.isDirectory()) {
			for (File f1 : file.listFiles()) {
				String f1Path = f1.getPath();
				if (f1Path.endsWith(".java")) {
					adi.add(f1Path);
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for (String s1 : adi) {
			BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(s1), "UTF-8"));
			String st = buf.readLine();
			while (st != null) {
				sb.append(st);
				sb.append("\n");
				st = buf.readLine();
			}
		}
		String sb1 = sb.toString();
		InputStream sbstream = new ByteArrayInputStream(sb1.getBytes(StandardCharsets.UTF_8));
		CompilationUnit cu = null;
		try {
			// parse the file
			cu = JavaParser.parse(sbstream);
		} catch (Exception e) {
		} finally {

			sbstream.close();
		}

		// prints the resulting compilation unit to default system output
		for (TypeDeclaration type : cu.getTypes()) {
			ArrayList<String> classnames = new ArrayList<String>();
			ArrayList<String> interfacenames = new ArrayList<String>();
			data tmp = new data();

			if (type instanceof ClassOrInterfaceDeclaration) {
				tmp.extended.add(((ClassOrInterfaceDeclaration) type).getExtends());
				// System.out.println("extends:"+((ClassOrInterfaceDeclaration)
				// type).getExtends());
				// tmp.implemented.add(((ClassOrInterfaceDeclaration)
				// type).getImplements());
				List impl = ((ClassOrInterfaceDeclaration) type).getImplements();
				for (int p = 0; p < impl.size(); p++) {
					tmp.implemented.add(impl.get(p));
					
				}
				if (((ClassOrInterfaceDeclaration) type).isInterface()) {
					classnames.add(type.getName());

					System.out.println("Interface->" + type.getName());
					tmp.isInter = true;
					tmp.classname = type.getName();
					intrfcnms.add(type.getName());
					clsnms+=" "+type.getName();
				} else {
					classnames.add(type.getName());
					interfacenames.add(type.getName());
					tmp.isInter = false;
					tmp.classname = type.getName();
					clsnms+=" "+type.getName();
					System.out.println("class->" + type.getName());
				}
			}

			for (BodyDeclaration member : type.getMembers()) {
				ArrayList<String> methodnames = new ArrayList<String>();
				ArrayList<String> parameternames = new ArrayList<String>();
				ArrayList<String> modifiernames = new ArrayList<String>();
				ArrayList<String> fieldnames = new ArrayList<String>();
				if(member instanceof ConstructorDeclaration)
				{
					tmp.constructor=((ConstructorDeclaration) member).getName()+"("+((ConstructorDeclaration) member).getParameters().toString().substring(1,((ConstructorDeclaration) member).getParameters().toString().length()-1)+")";
					for(int i=0;i<((ConstructorDeclaration) member).getParameters().size();i++){
						String constparam=((ConstructorDeclaration) member).getParameters().get(i).toString();
						constparam=constparam.substring(constparam.indexOf(' ')+1, constparam.length())+" : "+constparam.substring(0, constparam.indexOf(' '));
						tmp.cparams.add(constparam);
						uses.add(tmp.classname+":"+constparam);
					}
				}
				if (member instanceof MethodDeclaration) {
					methodnames.add(((MethodDeclaration) member).getName());
					tmp.mthd.prototype=((MethodDeclaration) member).getDeclarationAsString();
					List mystmt = null;
					try {
						mystmt = ((MethodDeclaration) member).getBody().getStmts();
						for (int i = 0; i < mystmt.size(); i++) {
							for(int j=0;j<classnames.size();j++){
								System.out.println("adi "+mystmt.get(i).toString()+" "+classnames.get(j).toString());
							
							if(mystmt.get(i).toString().contains(classnames.get(j).toString())){
								relations trel=new relations();
								trel.class1=tmp.classname;
								trel.class2=classnames.get(j).toString();
								trel.arrow="..>";
								rel.add(trel);
							
							}
							}
							if (mystmt.get(i).toString().contains("return")) {
								tmp.mthd.rstatement = mystmt.get(i).toString();
								System.out.println(tmp.mthd.mname+"\n"+tmp.mthd.rstatement);
							}
						}
					} catch (Exception e) {
					}
					tmp.mthd.mname.add(((MethodDeclaration) member).getName());
					tmp.mthd.acmmethod.add(ModifierSet.getAccessSpecifier(((MethodDeclaration) member).getModifiers())
							.getCodeRepresenation());
					tmp.mthd.rtypemethod.add(((MethodDeclaration) member).getType());
					String merged="";
					for(int i=0;i<((MethodDeclaration) member).getParameters().size();i++){
						
						String constparam=((MethodDeclaration) member).getParameters().get(i).toString();
						if(!constparam.equals("")){
							if(i!=((MethodDeclaration) member).getParameters().size()-1){
						merged+=constparam.substring(constparam.indexOf(' ')+1, constparam.length())+" : "+constparam.substring(0, constparam.indexOf(' '))+",";
							}else{
								merged+=constparam.substring(constparam.indexOf(' ')+1, constparam.length())+" : "+constparam.substring(0, constparam.indexOf(' '));
							}
					
						}
					}
					tmp.mthd.params.add(merged);
					
					//tmp.mthd.params.add(((MethodDeclaration) member).getParameters());
					//System.out.println("pup:"+((MethodDeclaration) member).getParameters().toString());
					// System.out.println(ModifierSet.getAccessSpecifier(((MethodDeclaration)
					// member).getModifiers()).getCodeRepresenation());
					// System.out.println(((MethodDeclaration)
					// member).getType());
					// System.out.println(((MethodDeclaration)
					// member).getName());

				} else if (member instanceof FieldDeclaration) {
					for (VariableDeclarator var : ((FieldDeclaration) member).getVariables()) {
						parameternames.add(var.getId().getName());
						modifiernames.add(ModifierSet.getAccessSpecifier(((FieldDeclaration) member).getModifiers())
								.getCodeRepresenation());

						tmp.fld.acm.add(ModifierSet.getAccessSpecifier(((FieldDeclaration) member).getModifiers())
								.getCodeRepresenation());
						tmp.fld.fname.add(var.getId().getName());
						tmp.fld.rtype.add(((FieldDeclaration) member).getType().toString());
						// System.out.println(((FieldDeclaration)
						// member).getType().toString());
						// System.out.println(var.getId().getName());
					}

				}

			}

			d1.add(tmp);
		}
		
		OutputStream png = null;
		String source = "@startuml\nskinparam classAttributeIconSize 0\n";
		
		
		for (int i = 0; i < d1.size(); i++) {
			data temp = d1.get(i);
			if (temp.isInter == false) {
				source += "Class " + temp.classname + " {\n";
				for (int k = 0; k < temp.fld.fname.size(); k++) {
					if(!temp.fld.rtype.get(k).toString().contains("Collection<") && !clsnms.contains(temp.fld.rtype.get(k).toString())){
					if (temp.fld.acm.get(k).equals("private"))
						source += "-" + temp.fld.fname.get(k) + " : " + temp.fld.rtype.get(k) + "\n";
					if (temp.fld.acm.get(k).equals("public"))
						source += "+" + temp.fld.fname.get(k) + " : " + temp.fld.rtype.get(k) + "\n";
					}
				}
				if(!temp.constructor.equals("")){
					source+="+"+temp.constructor.substring(0,temp.constructor.indexOf('('))+"(";
					for(int f=0;f<temp.cparams.size();f++){
						if(f!=temp.cparams.size()-1){
							source+=temp.cparams.get(f).toString()+",";
						}
						else{
							source+=temp.cparams.get(f).toString();
						}
					}
					source+=")\n";
				}
				
				for (int k = 0; k < temp.mthd.mname.size(); k++) {
					String returnvar="";
					if(temp.mthd.acmmethod.get(k).equals("public") && temp.mthd.mname.get(k).toString().contains("get")){
						returnvar=temp.mthd.rstatement.substring(temp.mthd.rstatement.indexOf(' ')+1, temp.mthd.rstatement.length()-1);
						if(returnvar.contains("this.")){
							returnvar=returnvar.substring(returnvar.indexOf('.')+1, returnvar.length());
						}
						System.out.println("Returns: " +returnvar);
						if(source.indexOf("-"+returnvar)!=-1){
						String newsource=source.substring(0,source.indexOf("-"+returnvar))+'+'+source.substring(source.indexOf("-"+returnvar)+1,source.length());
						source=newsource;
						}
					}
					
					if (temp.mthd.acmmethod.get(k).equals("public")
							&& !temp.mthd.mname.get(k).toString().contains("get")
							&& !temp.mthd.mname.get(k).toString().contains("set")){
						source += "+" + temp.mthd.mname.get(k)+"("+temp.mthd.params.get(k)+") : " + temp.mthd.rtypemethod.get(k) + "\n";
				}}
				source += "}\n";
			} else {
				source += "Interface " + temp.classname + " {\n";
				for (int k = 0; k < temp.fld.fname.size(); k++) {
					if (temp.fld.acm.get(k).equals("private"))
						source += "-" + temp.fld.fname.get(k) + " : " + temp.fld.rtype.get(k) + "\n";
					if (temp.fld.acm.get(k).equals("public"))
						source += "+" + temp.fld.fname.get(k) + " : " + temp.fld.rtype.get(k) + "\n";
				}
				for (int k = 0; k < temp.mthd.mname.size(); k++) {
					if (temp.mthd.acmmethod.get(k).equals("public")&& !temp.mthd.mname.get(k).toString().contains("get")
							&& !temp.mthd.mname.get(k).toString().contains("set")){
						source += "+" + temp.mthd.mname.get(k)+"("+temp.mthd.params.get(k)+") : " + temp.mthd.rtypemethod.get(k) + "\n";}}
				source += "}\n";

			}
		}
		
		for(int i=0;i<uses.size();i++){
			for(int j=0;j<intrfcnms.size();j++){
				if(uses.get(i).toString().contains(" "+intrfcnms.get(j).toString())){
					relations trel=new relations();
					trel.class1=uses.get(i).toString().substring(0,uses.get(i).toString().indexOf(':'));
					trel.class2=intrfcnms.get(j).toString();
					trel.arrow="..>";
					rel.add(trel);
				}
			}
		}
		
		
		for (int i = 0; i < d1.size(); i++) {
			data temp = d1.get(i);

			System.out.println(temp.classname + " "
					+ temp.extended.get(0).toString().substring(1, temp.extended.get(0).toString().length() - 1));
			if (temp.isInter == false) {
				for (int j = 0; j < temp.extended.size(); j++) {
					System.out.println(temp.classname + " " + temp.extended.size() + " " + temp.extended.get(0));

					if (!temp.extended.get(j).toString().substring(1, temp.extended.get(j).toString().length() - 1)
							.equals("")) {
						source += temp.extended.get(j).toString().substring(1,
								temp.extended.get(j).toString().length() - 1) + " <|-- " + temp.classname + "\n";
					}
				}
				for (int j = 0; j < temp.implemented.size(); j++) {
					System.out.println("in impl");
					System.out.println(temp.classname + " " + temp.implemented.size() + " " + temp.implemented.get(0));

					source += temp.implemented.get(j).toString() + " <|.. " + temp.classname + "\n";

				}

			}

		}

		ArrayList classnames = new ArrayList();
		
		ArrayList interfacenames= new ArrayList();
		try {
			for (int i = 0; i < d1.size(); i++) {
				classnames.add(d1.get(i).classname);
				// System.out.println("Aditya:"+d1.get(i).classname);
			}
			for (int i = 0; i < d1.size(); i++) {
				data temp = d1.get(i);
				
				for (int j = 0; j < temp.fld.rtype.size(); j++) {
					relations trel = new relations();
					for (int k = 0; k < classnames.size(); k++) {
						System.out.println("Adichauhan:"+temp.fld.rtype.get(j));
						l1:if (temp.fld.rtype.get(j).toString().equals(classnames.get(k).toString())) {
							for(int s=0;s<rel.size();s++){
								if(rel.get(s).arrow=="--" && rel.get(s).class1.equals(temp.classname) && rel.get(s).class2.equals(classnames.get(k))){
									break l1;
								}
								if(rel.get(s).arrow=="--" && rel.get(s).class2.equals(temp.classname) && rel.get(s).class1.equals(classnames.get(k))){
									break l1;
								}
							}
							trel.class1 = temp.classname.toString();
							trel.class2 = classnames.get(k).toString();
							trel.arrow = "--";
							for (int cur = 0; cur < d1.size(); cur++) {
								if (classnames.get(k).toString().equals(d1.get(cur).classname)) {
									for (int p = 0; p < d1.get(cur).fld.rtype.size(); p++) {
										if (d1.get(cur).fld.rtype.get(p).toString().equals(temp.classname.toString())) {
											trel.rel1 = "1";
											trel.rel2 = "1";
											System.out.println(trel.class1 + " \"" + trel.rel1 + "\" " + trel.arrow
													+ " \"" + trel.rel2 + "\" " + trel.class2);
										}
									}
								}
							}
							rel.add(trel);
						}

						l2:if (temp.fld.rtype.get(j).equals(classnames.get(k))) {
							for(int s=0;s<rel.size();s++){
								if(rel.get(s).arrow=="--" && rel.get(s).class1.equals(temp.classname) && rel.get(s).class2.equals(classnames.get(k))){
									break l2;
								}
								if(rel.get(s).arrow=="--" && rel.get(s).class2.equals(temp.classname) && rel.get(s).class1.equals(classnames.get(k))){
									break l2;
								}
							}
							
							trel.class1 = temp.classname.toString();
							trel.class2 = classnames.get(k).toString();

							trel.arrow = "--";
							for (int cur = 0; cur < d1.size(); cur++) {
								if (classnames.get(k).toString().equals(d1.get(cur).classname)) {
									for (int p = 0; p < d1.get(cur).fld.rtype.size(); p++) {
										if (d1.get(cur).fld.rtype.get(p).toString()
												.equals("Collection<" + temp.classname.toString() + ">")) {
											trel.rel1 = "0..*";
											trel.rel2 = "1";
											System.out.println(trel.class1 + " \"" + trel.rel1 + "\" " + trel.arrow
													+ " \"" + trel.rel2 + "\" " + trel.class2);
										}
									}
								}
							}
							rel.add(trel);
						}

						l3:if (temp.fld.rtype.get(j).equals("Collection<" + classnames.get(k) + ">")) {
							for(int s=0;s<rel.size();s++){
								if(rel.get(s).arrow=="--" && rel.get(s).class1.equals(temp.classname) && rel.get(s).class2.equals(classnames.get(k))){
									break l3;
								}
								if(rel.get(s).arrow=="--" && rel.get(s).class2.equals(temp.classname) && rel.get(s).class1.equals(classnames.get(k))){
									break l3;
								}
							}
							
							trel.class1 = temp.classname.toString();
							trel.class2 = classnames.get(k).toString();

							trel.arrow = "--";
							for (int cur = 0; cur < d1.size(); cur++) {
								if (classnames.get(k).toString().equals(d1.get(cur).classname)) {
									for (int p = 0; p < d1.get(cur).fld.rtype.size(); p++) {
										if (d1.get(cur).fld.rtype.get(p).toString().equals(temp.classname.toString())) {
											trel.rel1 = "1";
											trel.rel2 = "0..*";
											System.out.println(trel.class1 + " \"" + trel.rel1 + "\" " + trel.arrow
													+ " \"" + trel.rel2 + "\" " + trel.class2);
										}
									}
								}
							}
							rel.add(trel);
						}
						l4:if (temp.fld.rtype.get(j).equals("Collection<" + classnames.get(k) + ">")) {
							for(int s=0;s<rel.size();s++){
								if(rel.get(s).arrow=="--" && rel.get(s).class1.equals(temp.classname) && rel.get(s).class2.equals(classnames.get(k))){
									break l4;
								}
								if(rel.get(s).arrow=="--" && rel.get(s).class2.equals(temp.classname) && rel.get(s).class1.equals(classnames.get(k))){
									break l4;
								}
							}
							trel.class1 = temp.classname.toString();
							trel.class2 = classnames.get(k).toString();

							trel.arrow = "--";
							for (int cur = 0; cur < d1.size(); cur++) {
								if (classnames.get(k).toString().equals(d1.get(cur).classname)) {
									for (int p = 0; p < d1.get(cur).fld.rtype.size(); p++) {
										if (d1.get(cur).fld.rtype.get(p).toString()
												.equals("Collection<" + temp.classname.toString() + ">")) {
											trel.rel1 = "0..*";
											trel.rel2 = "0..*";
											System.out.println(trel.class1 + " \"" + trel.rel1 + "\" " + trel.arrow
													+ " \"" + trel.rel2 + "\" " + trel.class2);
										}
									}
								}
							}
							rel.add(trel);
						}

					}

				}

			}
			
			ArrayList<relations> relfinal = new ArrayList<relations>();
			
			
			
			for (int s = 0; s < rel.size(); s++) {
				if (!rel.get(s).rel1.equals("") && !rel.get(s).rel2.equals("")) {
					source += rel.get(s).class1 + " \"" + rel.get(s).rel1 + "\" " + rel.get(s).arrow + " \""
							+ rel.get(s).rel2 + "\" " + rel.get(s).class2 + "\n";
				}
				else
				{
					source += rel.get(s).class1 + " " + rel.get(s).arrow + " " + rel.get(s).class2 + "\n";
				}
			}
			for (int i = 0; i < d1.size(); i++) {
				if(d1.get(i).isInter==true){
				interfacenames.add(d1.get(i).classname);
				 //System.out.println("Aditya:"+d1.get(i).classname);
			}}
			for (int i = 0; i < d1.size(); i++) {
				data temp = d1.get(i);
				for(int p=0; p< interfacenames.size();p++)
				{
				adi:	for(int q=0;q< temp.mthd.params.size();q++)
					{
						//System.out.println("adit");
						//System.out.println(temp.mthd.params.get(q).toString()+" "+interfacenames.get(p).toString());
						if(temp.mthd.params.get(q).toString().contains(interfacenames.get(p).toString()) && !temp.isInter )
						{
							source+=temp.classname+" ..> "+interfacenames.get(p).toString()+"\n";
							break adi;
						}
					}
				}
			}
		} catch (Exception e) {
		}
		// source +="Class05 o-- Class06\n";
		// source +="Class03 *-- Class04\n";
		// source +="Class01 *-- Class02\n";
		source += "@enduml\n";
		System.out.println(source);
		SourceStringReader reader = new SourceStringReader(source);
		// Write the first image to "png"
		png = new FileOutputStream(args[1]);
		String desc = reader.generateImage(png);

	}

}
