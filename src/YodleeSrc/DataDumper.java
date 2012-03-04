/*
 * Copyright 2007 Yodlee, Inc.  All Rights Reserved.  Your use of this code 
 * requires a license from Yodlee.  Any such license to this code is 
 * restricted to evaluation/illustrative purposes only. It is not intended 
 * for use in a production environment, and Yodlee disclaims all warranties 
 * and/or support obligations concerning this code, regardless of the terms 
 * of any other agreements between Yodlee and you."
 */
package YodleeSrc;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yodlee.soap.core.dataservice.YDate;
import com.yodlee.soap.core.dataservice.YMoney;

public class DataDumper {

    public static void dumper(Object o) throws Exception {
        if(o == null) {
            System.out.println("null");
            return ;
        }
        Class   c   =   o.getClass() ;

        if (c == null) {
            System.err.println("no class for this object:" + o);
            return ;
        }
        if (canHandleAsPrimitive(o,c)) {
            return ;
        }

        if (o instanceof List) {
            handleList((List)o) ;
            return ;
        }

        if (o instanceof Map) {
            handleMap((Map)o) ;
            return ;
        }

        Method  methods[]   =   c.getMethods() ;
        for (int mi = 0; mi < methods.length; mi++) {
            Method method       =   methods[mi];
            if (methodsOfObject(method)) continue ;

            Class inParams[]    =   method.getParameterTypes() ;
            if (inParams.length != 0) {
                // Only intersted in methods that do not take any args.
                continue ;
            }
            System.out.print("the value for:"+ c.getName() + "."  + method.getName() + " is:");
            Object result       =   null ;
            result = method.invoke(o, (Object[]) null);

            if (result != null)
                DataDumper.dumper(result) ;
            else
                System.out.println("");
        }
    }

    private static void handleMap(Map map) throws Exception{
        Set keys    =   map.keySet() ;
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            System.out.println("The Key is -");
            DataDumper.dumper(o);
            System.out.println();
            System.out.println("The Value is -");
            DataDumper.dumper(map.get(o));
            System.out.println();
        }
    }

    private static void handleList(List l) throws Exception {
        for (Iterator iterator = l.iterator(); iterator.hasNext();) {
            Object o = (Object) iterator.next();
            DataDumper.dumper(o);
        }
    }

    private static boolean methodsOfObject(Method method) {
        String  name    =   method.getName() ;
        if ( (name.equals("notify"))
             || (name.equals("notifyAll"))
             || (name.equals("getClass"))
             || (name.equals("hashCode"))
             || (name.equals("wait"))
             || (name.equals("equals"))
             || (name.equals("toString"))
             || (name.equals("clone"))
            ) {
            return true ;
        }
        return false;
    }

    private static boolean canHandleAsPrimitive(Object o, Class c) {
        if (    (c.getName().equals(String.class.getName()))
                || (c.getName().equals(Long.class.getName()))
                || (c.getName().equals(BigDecimal.class.getName()))
                || (c.getName().equals(Class.class.getName()))
                || (c.getName().equals(Integer.class.getName()))
                || (c.getName().equals(Object.class.getName()))
                || (c.getName().equals(Boolean.class.getName()))
                || (c.getName().equals(Float.class.getName()))
                || (c.getName().equals(Double.class.getName()))
                || (c.getName().equals(StringBuffer.class.getName()))
                || (c.getName().equals(YDate.class.getName()))
                || (c.getName().equals(YMoney.class.getName()))
        ) {
            System.out.println(o);
            return true ;
        } else {
            return false;
        }
    }
}


