package com.stackroute.datamunger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.stackroute.datamunger.query.parser.QueryParser;



public class DataMunger {

		
	public static void main(String[] args) {
		
		
		//read the query from the user
		Scanner input = new Scanner(System.in);
		String QueryString = input.nextLine();
		
		input.close();
		//create an object of QueryParser class
//		DataMunger dataMunger = new DataMunger();
		QueryParser queryParser = new QueryParser();
		
		
		/*
		 * call parseQuery() method of the class by passing the query string which will
		 * return object of QueryParameter
		 */
		queryParser.parseQuery(QueryString);

	}
		
		
}
