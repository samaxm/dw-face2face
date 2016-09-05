package online.decentworld.face2face.actor;

import java.util.Comparator;
import java.util.function.Consumer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class TestActors extends UntypedActor{

	@Override
	public void onReceive(Object arg0) throws Throwable {
		if(arg0 instanceof String)
			Thread.sleep(1000);
			System.out.println("coming up ------>"+arg0+"sender#"+getSender().equals(ActorRef.noSender()));
			getSender().tell("bbbbbbbbb", ActorRef.noSender());	
			Comparator<String> byLex=(String a,String b)->a.hashCode()-b.hashCode();
	}

	
	public static void main(String[] args) throws InterruptedException {
//		Consumer<String> run=(String word)->{System.out.println(word);};
//		Integer::parseInt
//		(String a) -> a.length();
		Test t=new Test();
		t.doSome("java",System.out::println,System.out::println);
		Thread.sleep(1000);
	}
	
	public static class Hello{
		public static void say(){
			System.out.println("hi");
		}
	}
	

	public static class Test{
		public void doSome(String word,Consumer<String> consumer,Consumer<String> consumer2){
			
		}
	} 
}
