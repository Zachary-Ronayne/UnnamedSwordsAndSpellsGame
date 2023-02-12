package tester;

import zgame.core.Game;
import zgame.core.utils.ClassMappedList;
import zgame.core.utils.Uuidable;

import java.util.UUID;

/** A class purely for making and testing random specific bits of code */
public class DebugGame extends Game{
	
	public static class A{
		public String a;
		public A(String a){
			this.a = a;
		}
	}
	public static class B extends A{
		public B(String a){
			super(a);
		}
	}
	public static class C extends B implements Uuidable{
		String uuid = UUID.randomUUID().toString();
		public C(String a){
			super(a);
		}
		
		@Override
		public String getUuid(){
			return uuid;
		}
	}
	
	public static void main(String[] args){
//		var g = new DebugGame();
//		g.start();
		
		ClassMappedList list = new ClassMappedList();
		list.addClass(A.class);
		list.addClass(B.class);
		list.addClass(C.class);
		
		var obj1 = new A("1");
		var obj2 = new A("2");
		var obj3 = new B("3");
		var obj4 = new B("4");
		var obj5 = new C("5");
		var obj6 = new C("6");
		
		list.add(obj1);
		list.add(obj2);
		list.add(obj3);
		list.add(obj4);
		list.add(obj5);
		list.add(obj6);
		
		for(var l : list.get().entrySet()){
			System.out.println("[" + l.getKey() + "]");
			for(var c : l.getValue()){
				System.out.println(c + " | " + c.getClass() + " | " + ((A)c).a);
			}
			System.out.println("------------");
		}
		
		System.out.println("------------------------------------------------");
		
		list.remove(obj1);
		list.remove(obj3);
		list.remove(obj5);
		
		for(var l : list.get().entrySet()){
			System.out.println("[" + l.getKey() + "]");
			for(var c : l.getValue()){
				System.out.println(c + " | " + c.getClass() + " | " + ((A)c).a);
			}
			System.out.println("------------");
		}
	}
	
}
