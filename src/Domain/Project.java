package Domain;

import java.util.List;

public class Project {
	public List<Entity> entities;
	public List<ClassDiagramArrow> arrows;

	public Project(List<Entity> entities, List<ClassDiagramArrow> arrows) {
		this.entities = entities;
		this.arrows = arrows;
	}
}
