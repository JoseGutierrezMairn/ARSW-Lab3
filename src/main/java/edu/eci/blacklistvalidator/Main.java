/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]) throws InterruptedException{
    	//int n = Runtime.getRuntime().availableProcessors(); Número de núcleos de procesamiento
    	//System.out.println(n);
    	long startTime = System.currentTimeMillis();
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> blackListOcurrences=hblv.checkHost("202.24.34.55",1);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        long endTime = System.currentTimeMillis();
    }
    
}
