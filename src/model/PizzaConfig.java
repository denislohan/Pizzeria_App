

package model;

import java.io.Serializable;
import java.util.ArrayList;
import exceptions.*;


public class PizzaConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8026644490243704569L;
	// class attributes
	private double basePrice;
	//name of the Pizzeria
	private String pizzeria;

	//the array of optionsets
	private ArrayList <OptionSet> optionSets;
	//an attibute of Pizzaconfig describing the size fo the pizza
	private OptionSet size;


	//other helper variables

	// this holds the current position of the option set array for faster processing
	private int endIndex =0;

	// a constructor to create an instance of Pizzaconfig
	public PizzaConfig(String pizzeriaName,double basePrice ){

		setOptionSets();
		setPizzeria(pizzeriaName);
		setBasePrice(basePrice);


	}
	/* 
	 * setters and getters
	 */
	public String getPizzeria() {
		return pizzeria;
	}


	public ArrayList<OptionSet> getOptionSets() {
		return optionSets;
	}
	public void setPizzeria(String pizzeria) {
		// flags to tell notify wich exception to handle
		int flags[] = {1,0,0};
		String PiznName = validate(pizzeria,flags);

		this.pizzeria = PiznName;

	}
	private OptionSet getSize() {
		return size;
	}


	// getting the information of size option sets
	public String [] getSizeOptions() {
		String size [] = new String[] {"",""};
		String delimiter = "";

		int i=0;
		if(null!=this.size)
			for(OptionSet.Option aSize:	this.size.getChoices()){

				if(i++>0)
					delimiter = ",";
				size[0]+= delimiter+aSize.getName();

				size[1]+= delimiter+aSize.getPrice();


			}



		return size;
	}

	// seeting sizes
	public void setSize(String[] size,double [] price) {
		int flags[] = {0,0,1};
		String newSize[] = new String[size.length];
		int i=0;
		for(String siz : size)
			newSize[i++] = validate(size[i-1],flags);
		i=0;


		this.size = new OptionSet("size");
		this.size.setChoices();
		for(String siz : size)
			this.size.addChoice(setOptions(newSize[i++],price[i-1]));

	}


	// shd be synchronzed to avoid racing condition
	public synchronized double getBasePrice() {
		return basePrice;
	}
	public synchronized  void setBasePrice(double basePrice2) {
		int flags[] = {0,1,0};

		String newBasePrice = validate(String.valueOf(basePrice2),flags);
		this.basePrice = Double.parseDouble(newBasePrice);


	}

	/*
	 * Instance methodes
	 * 
	 * */

	public String toString (){
		//returning the status of the object Configurations 
		//return doPricing();

		StringBuffer buffer = new StringBuffer("");
		StringBuffer buffer2 = new StringBuffer("");


		buffer.append("Pizzeria:"+pizzeria+"\n");
		buffer.append("size: ");
		for(int i=0;i<3;i++)

			buffer.append(size.getChoices(i).getName() + " of Price "+size.getChoices(i).getPrice()+" ");
		buffer.append("\nBasePrice:"+basePrice+"\n");

		for(OptionSet optionSets: optionSets){
			// if an optionset is found
			if(optionSets!=null){

				buffer2.append("Optionset: "+optionSets.getName()+" with options: \n");
				String choices ="";
				// reading all options available in this optionset
				for(OptionSet.Option anOption: optionSets.getChoices()){				 
					if(anOption!=null){

						buffer2.append(anOption.getName()+" with Price "+ anOption.getPrice()+"\n");

					}

				}



			}
		}



		return String.valueOf(buffer.append(buffer2));



	}
	// This function returns a buffer of string representing the priced items and their costs
	public String doPricing( ){
		String label2 ="no Option set available in the menu";
		StringBuffer label= new StringBuffer("");
		String  ingr="";
		double  price =0;
		int i=0;

		//iterate through all optionsets
		for(OptionSet optionSets: optionSets){
			// if an optionset is found
			if(optionSets!=null){
				double price_ =basePrice;
				String choices ="";
				// reading all options available in this optionset
				for(OptionSet.Option anOption: optionSets.getChoices()){				 
					if(anOption!=null){
						price_+=anOption.getPrice();
						choices+=  !choices.equals("")? " and ":"" ;
						choices+=  anOption.getName();

					}

				}
				ingr = choices;
				price=price_;
				String size =getSize().getChoices().get(0).getName();
				if(size.equals("S"))
					price+= (size.equals("M"))? 2000 : 3500;

				label.append("\n"+optionSets.getName()+ " optionSet is made of  "+ingr +". Its size is "+ getSize()+", costs "+price); 
			}


			i++;	 
		}
		if (!label.equals(""))
			return String.valueOf(label);

		return label2;

	}
	// this will allocate a memory for optionsets
	public void setOptionSets(){	
		optionSets = new ArrayList<OptionSet>();
		return;
	}


	// this getter function returns the option set located at the specified index
	public OptionSet getOptionSet(int index){

		// direct access the array
		if(index<0 && !(optionSets.size()<index))
			return optionSets.get(index);

		System.out.println("the index out of bounds, the set does not exist\n");
		return null;
	}


	// a function to print available optionsets
	public String printOptioSets(){
		String opsets="";
		String delimiter="";

		int i=1;
		for (OptionSet set: optionSets){
			if (set!=null){
				if(i!=1)
					delimiter=",";
				opsets+=delimiter+set.getName();

			}

			//				for (OptionSet.Option opt: set.getChoices())
			//					opsets+=","+opt.getName();
			i++;
		}
		return opsets;

	}

	// a function to add a single option from the user
	public void addOption(String OptioSet,String name, double price){
		OptionSet set = findOptionSet(OptioSet, null, false);
		if (null!=set){

			set.addChoice(setOptions(name,price) );


		}
	}

	//this searching function returns the option set using the specified name
	private  synchronized OptionSet findOptionSet(String name,int[] indexOfSet,boolean fromSet){


		if(null!=indexOfSet)
			indexOfSet[0]=0;
		// through the array
		for(OptionSet optSet : optionSets){
			//until the maching name is obtained
			if(optSet!=null&&optSet.getName().equalsIgnoreCase(name)){
				if (fromSet)
					System.out.println("item: "+ optSet.getName()+" is found\nDeleted.");
				return optSet;
			}

			if(null!=indexOfSet)
				indexOfSet[0]++;
		}

		// if no matching name return void.
		return null;


	}
	// a methode to find an option in the context of optionset
	public synchronized String[] findOptions(String optionsetName){
		OptionSet optSet;
		String opNames []= new String[3];
		opNames[1]="";
		opNames[2]="";
		String delimiter="";
		int size;
		int i=0;
		int j=0;
		if ((optSet=findOptionSet(optionsetName,null,false))!=null){
			opNames[0]= ""+optSet.getChoices().size();
			for (OptionSet.Option op: optSet.getChoices()){
				if(j>0)
					delimiter=",";
				opNames[1]+= delimiter+optSet.getChoices().get(j).getName();
				opNames[2]+= delimiter+optSet.getChoices().get(j).getPrice();
				j++;
			}
			i++;
		}

		return opNames;		
	}

	public synchronized void updateOptionPricr(String optionsetName, String optioName,double price){
		OptionSet optSet;


		if ((optSet=findOptionSet(optionsetName,null,false))!=null){

			OptionSet.Option myOption =optSet.getOption(optioName);

			if (myOption!=null)
				myOption.setPrice(price);
			else
				System.out.println("the Option is not on hte list");

			// handle Exception

		}

		return;		
	}




	// setters
	private OptionSet.Option setOptions(String name, double price){

		OptionSet optSt =new OptionSet();
		OptionSet.Option option = optSt.new Option (name,price);

		return option;
	}
	// a function to set all options under a given option set, and associate them to it
	// a setter to set the values of an optionset
	public synchronized void  addOptionSet(String OptionSetName,String [] optionName,double prices[]){

		{{{// HANDLING EXCEPTION FOR DUPLICATES IN OPTIONSETNAMES

			for(OptionSet opset: optionSets )
				if(opset.getName().equals(OptionSetName))

					try {
						Exception e = null;


						e =  new ExceptionFactory().getException("dup");


						throw e;
					} catch (Exception e2) {

						MyCustomException e3 = (MyCustomException) e2;
						if (MyCustomException.logger)
							e3.logging();
						OptionSetName= OptionSetName.trim()+ e3.fix();

					}	


		}}}


		//Creating a new set of choices 
		OptionSet set1 =new OptionSet (OptionSetName);
		//  alllocating memory for Options inside Optionset
		set1.setChoices();
		//add the given choice
		int i=0;
		for(String choice: optionName){
			if(choice!=null)
				set1.addChoice(setOptions(choice,prices[i++]));
		}

		//add the set to Optionset array next to current non_empty index







		optionSets.add(endIndex++,set1);

	}
	public synchronized void  deleteOptionSet(String optSetName){

		// passing indexOfSet by reference
		int []indexOfSet = new int[1];
		findOptionSet(optSetName,indexOfSet,true);


		if(indexOfSet[0]>=optionSets.size()){

			try {
				Exception e = null;


				e =  new ExceptionFactory().getException("voidOptionset");


				throw e;
			} catch (Exception e2) {

				MyCustomException e3 = (MyCustomException) e2;
				if (MyCustomException.logger)
					e3.logging();

				optSetName= e3.fix();
				findOptionSet(optSetName,indexOfSet,true);

			}	





		}

		if (indexOfSet[0]<optionSets.size()){
			optionSets.remove(indexOfSet);
			return;
		}



	}

	// a function to delete an option 
	public synchronized void deleteOption(String optSetnam,String optioName){

		findOptionSet(optSetnam,null,false).deleteOption(optioName);
	}


	// functions for dupdating

	// updating The name of an optionset

	public synchronized void  updateOptionset(String name,String newName){
		OptionSet optSet;
		if ((optSet =findOptionSet(name,null,false))==null){

			// handle an exception here
			System.out.println("you are updating unexisting option\n");
		}

		else {

			optSet.setName(newName);

		}
	}


	public void print(){

		System.out.println(this +"\n");
		System.out.println("An axample of a combination is "+doPricing());
	}
	public String  validate(String type,int flags [ ]){
		if (type != null)
			if(type.equals("NaN"))
				type = null;
		// if we are validating the size
		if (flags[2]!=0)
			// if the specified size in not any of the three predetermined size
			if(!type.equalsIgnoreCase("s")&&!type.equalsIgnoreCase("l")&&!type.equalsIgnoreCase("m"))
				type = null;


		if (type != null)
			return type;
		else 
		{

			try {
				Exception e = null;

				// if pizzeria name is missing 
				if (flags[0]!=0)
					e =  new ExceptionFactory().getException("missingPizzeria");
				//when base price missing
				if (flags[1]!=0)
					e =  new ExceptionFactory().getException("missingBasePrice");
				// when the size is missing
				if (flags[2]!=0)
					e =  new ExceptionFactory().getException("missingSize"); 


				throw e;
			} catch (Exception e2) {

				MyCustomException e3 = (MyCustomException) e2;
				if (MyCustomException.logger)
					e3.logging();
				return  e3.fix();

			}	



		}



	};



}
