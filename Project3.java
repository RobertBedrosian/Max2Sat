/*Robert Bedrosian
*COMP 482
*Professor Noga
*This program determines max 2-Sat of a given set of clauses.
*The bruteForce method checks all possible sets of truth values
*and recursively returns the set of truth values that have the highest
*amount of true clauses.
*
*The fastsol is a greedy approach to the problem, where it checks whether there
*are more true or not true instances of each variable. the maximum of those
*two numbers designates the truth value for that variable. Then count the 
*amount of true clauses.
*Runtime: Fastsolution, O(n^2) /// Optimal Solution, O(n^2*2^n)
*/


import java.io.*;
import java.util.*;

public class Project3
{
	//Check each pair, if one value is positive (True), then the pair is true and the amount of total truth values can be incremented to record the total amount of true clauses.
	public static int countTruthValues(ArrayList<Integer> pairs)
	{
		int totalTruthVal=0;
		for (int i=0;i<pairs.size();i= i+2)
		{
			if (pairs.get(i) > 0 || pairs.get(i+1) > 0)
				totalTruthVal++;
		}
		return totalTruthVal;
	}
	
	//Reconfigure the actual values of the list of inputs by multiplying the given truth value to it's corresponding value in the list of pairs. This will give us the actual truth value per pair.
	public static ArrayList<Integer> setActualTruthValues(ArrayList<Integer> pairs, int[]variablesVal, ArrayList<Integer> variables)
	{
		ArrayList<Integer> newPairs=new ArrayList<Integer>();
		for (int i=0;i<pairs.size();i++)
		{
			newPairs.add(pairs.get(i));
		}
		for (int i=0; i<variablesVal.length;i++)
		{
			for (int j=0; j<pairs.size(); j++)
			{
				if (Math.abs(pairs.get(j)) == variables.get(i))
				{
					int replacement = pairs.get(j);
					newPairs.set(j,replacement * variablesVal[i]);
				}
			} 
		}
		return newPairs;
	}

	public static int[] bruteForce(int[] variablesVal,ArrayList<Integer> pairs, ArrayList<Integer> variables, int counter)
	{
		//If the array of truth values for variables is full, all variables have a truth value. Base case. Count the total amount of truth statements and return an array of Length +1 with truth values and amount of true clauses.
		if (counter == variablesVal.length)
		{
			int [] solution = new int [variablesVal.length+1];
			ArrayList<Integer> newPairs=new ArrayList<Integer>();
			int totalTruthVal=0;
			//Fill the solution array with this recursive calls set of truth values.
			for (int i=0; i < variablesVal.length;i++)
			{
				solution[i]=variablesVal[i];
			}
			newPairs=setActualTruthValues(pairs, variablesVal, variables);
			solution[solution.length-1]=countTruthValues(newPairs);
			return solution;
		}
		else
		{
			//If there is still an element that needs a truth value, continue assigning new truth values.
			//Perform two recursive calls where the next available truth value is set to positive and negative. This will cover all possible values.
			variablesVal[counter]=1;
			counter++;
			int []firstSol= bruteForce(variablesVal, pairs, variables,counter );
			counter--;
			variablesVal[counter]=-1;
			counter++;
			int []secondSol= bruteForce(variablesVal, pairs, variables,counter );
			//Take the solution with the maximum amount of true clauses.
			if (firstSol[firstSol.length-1] > secondSol[secondSol.length-1])
				return firstSol;
			else
			{
				return secondSol;
			}
		}
	}

	//Greedy Solution
	public static int [] fastSol(int[] variablesVal,ArrayList<Integer> pairs, ArrayList<Integer> variables)
	{
		int totalTruthVal=0;
		int [] solution = new int [variablesVal.length+1];
		int [] variableCount=new int[variablesVal.length * 2];
		//Check every input against all variables to determine whether or not it is a positive or negative input value per variable. Count this number.
		for (int i=0;i<pairs.size();i++)
		{
			for(int j=0; j<variables.size();j++)
			{
				if (pairs.get(i)==variables.get(j))
				{
					variableCount[j*2]++;
					break;
				}
				else if (pairs.get(i)== -1 * variables.get(j))
				{
					variableCount[j*2+1]++;
					break;
				}
			}
		}
		int count=0;
		//Set the truth values per variable by determining which value (true or false) appears more often.
		for (int i=0;i<variableCount.length;i=i+2)
		{
			if(variableCount[i]>variableCount[i+1])
			{
				variablesVal[count]=1;
				count++;
			}
			else
			{
				variablesVal[count]=-1;
				count++;
			}
		}
		pairs=setActualTruthValues(pairs, variablesVal, variables);
		//Fill the solution array with the set of truth values.
		for (int i=0; i < variablesVal.length;i++)
		{
			solution[i]=variablesVal[i];
		}
		solution[solution.length-1]=countTruthValues(pairs);
		return solution;
	}

	public static void main(String[] args)
	{
		//Arraylist for dynamic size arrays of variables,pairs.
		ArrayList<Integer> variables = new ArrayList<Integer>();
		ArrayList<Integer> pairs = new ArrayList<Integer>();
		String fileName="input3.txt";
		File file = new File(fileName);
		int valueToCheck;
		try
		{
			Scanner sc=new Scanner(file);
			while (sc.hasNextInt())
			{
				valueToCheck=sc.nextInt();
				pairs.add(valueToCheck);
				if (variables.indexOf(Math.abs(valueToCheck)) == -1)
					variables.add(Math.abs(valueToCheck));
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
        for (int i=0;i<variables.size();i++)
        {
        	int position=i;
        	for (int j=i;j<variables.size();j++)
        	{
        		if (variables.get(j) < variables.get(position))
        			position = j;
        	}
        	int min = variables.get(position);
        	variables.set(position, variables.get(i));
        	variables.set(i,min);
        }
        //Setup environment for the call to a solution. An array is needed to pass into the function which will hold the set of truth values. Another array is needed to hold the actual solution returned by the method. also make sure variables are sorted smallest to largest.
		int []variablesval=new int[variables.size()];
		int []actualsolution=new int[variables.size()+1];
		Scanner userinput= new Scanner(System.in);
		System.out.println("Would you like");
		System.out.println("1) True Optimal Solution (slow)");
		System.out.println("2) Pretty Good Solution (fast)");
		int input =userinput.nextInt();
		userinput.close();
		if (input==1)
		{
			actualsolution=bruteForce(variablesval, pairs, variables,0);
			System.out.print(actualsolution[actualsolution.length-1] + " ");
			for (int i=0; i< actualsolution.length-1;i++)
			{
				if (actualsolution[i]==1)
					System.out.print("T");
				else
				{
					System.out.print("F");
				}
			}
			System.out.println("");
		}
		else if (input==2)
		{
			actualsolution=fastSol(variablesval, pairs, variables);
			System.out.print(actualsolution[actualsolution.length-1] + " ");
			for (int i=0; i< actualsolution.length-1;i++)
			{
				if (actualsolution[i]==1)
					System.out.print("T");
				else
				{
					System.out.print("F");
				}
			}
			System.out.println("");
		}
	}
}
