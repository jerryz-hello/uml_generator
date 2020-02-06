package Presentation;

import java.io.IOException;

import Domain.MainLogic;

public class Main {
	public static void main(String args[]) throws IOException {
		MainLogic m = new MainLogic(args, "happy_path.svg");
		m.run();
	}
}
