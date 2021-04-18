package fr.feavy.java.map;

public class Coordonate {

	private int x, y;
	
	public Coordonate(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Coordonate){
			Coordonate obj2 = (Coordonate)obj;
			return (x == obj2.x && y == obj2.y);
		}else
			return false;
	}
	
	@Override
	public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
}
