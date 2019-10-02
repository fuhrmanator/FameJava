//  Copyright (c) 2007-2008 Adrian Kuhn <akuhn(a)iam.unibe.ch>
//  
//  This file is part of 'Fame (for Java)'.
//  
//  'Fame (for Java)' is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your
//  option) any later version.
//  
//  'Fame (for Java)' is distributed in the hope that it will be useful, but
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
//  License for more details.
//  
//  You should have received a copy of the GNU Lesser General Public License
//  along with 'Fame (for Java)'. If not, see <http://www.gnu.org/licenses/>.
//  

package ch.akuhn.fame.codegen;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class JavaFile {

    private StringBuilder body;
    private Map<String, String> imports;
    private Map<String, String> properties;
    private String myPackage;
    private String name;
    private String superName;
    private String modelPackagename;
    private String modelClassname;

    public JavaFile(String myPackage, String name) {
        this.myPackage = myPackage;
        this.name = name;
        this.body = new StringBuilder();
        this.imports = new HashMap();
        this.properties = new HashMap();
    }

    public <T> void addImport(Class<T> tee) {
        this.addImport(tee.getPackage().getName(), tee.getSimpleName());
    }

    public void addImport(String aPackage, String className) {
        //if (aPackage.equals(myPackage)) return;
        if (aPackage.equals("java.lang")) return;
        if (aPackage.equals("java.util")) return;
        imports.put(className, "../"+aPackage + "/" + convertToFilename(className));
    }
    
    public static String convertToFilename(String classname) {
      StringBuffer buffer = new StringBuffer();
    	
      for (int i = 0; i < classname.length(); i++) {
    	Character c = new Character(classname.charAt(i));
    	if(Character.isUpperCase(c)) {
    		if (i>0) {
    			buffer.append("_");
    		}
    		buffer.append(Character.toLowerCase(c));
    	} else {
    		buffer.append(c);
    	}
      }	
      return buffer.toString();
    }
    
    public void addProperty(String name, String getter) {
    	properties.put(name, getter);
    }
    
    public String getProperties() {
    	StringBuilder stream = new StringBuilder();
    	for ( Entry<String, String> each: properties.entrySet()) {
    		stream.append("    exporter.addProperty(\""+each.getKey()+"\", this."+each.getValue()+"());\n");
    	}
    	return stream.toString();
    }
    

    public void addSuperclass(String aPackage, String className) {
        if (className.equals("Object") && aPackage.equals("java.lang")) {
        	aPackage="..";
        	className="FamixBaseElement";
        }
        this.addImport(aPackage, className);
        this.superName = className;
    }

    public void generateCode(Appendable stream) throws IOException {
        Template template = Template.get("Class");
        template.set("PACKAGE", myPackage);
        template.set("AUTOGENCODE", "Automagically generated code");
        template.set("THISTYPE", name);
        template.set("THISPACKAGE", modelPackagename);
        template.set("THISNAME", modelClassname);
        template
                .set("EXTENDS", superName == null ? "" : "extends " + superName);
        template.set("IMPORTS", getImports());
        template.set("FIELDS", "");
        template.set("METHODS", getContentStream().toString());
        template.set("PROPERTIES", getProperties());
        stream.append(template.apply());
    }

    public StringBuilder getContentStream() {
        return body;
    }

    public String getImports() {
        StringBuilder stream = new StringBuilder();
        for (Entry<String, String> each : imports.entrySet()) {
            stream.append("import {").append(each.getKey()).append("} from \"./").append(each.getValue()).append("\";\n");
        }
        return stream.toString();
    }

    public String getModelClassname() {
        return modelClassname;
    }

    public String getModelPackagename() {
        return modelPackagename;
    }

    public void setModelClassname(String modelClassname) {
        this.modelClassname = modelClassname;
    }

    public void setModelPackagename(String modelPackagename) {
        this.modelPackagename = modelPackagename;
    }

}
