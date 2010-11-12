package framework.items;

public class StringItem extends ZimbraItem{

	private String string;
	private int order; //the order of the element within the outer list
	
	public StringItem (String string,int order) {
		this.string=string;
		this.order=order;
	}
	
	public String getItem(){
		return string;
	}
	
	public void setItem(String string) {
		this.string=string;
	}

	public void setOrder(int order){
		this.order=order;
	}
	
	public int getOrder(){
		return order;
	}
	
}
