package DataSource;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class ASMSequenceMethodVisitor extends MethodVisitor {

	private String owner;
	private String name;
	private List<String> types;
	private boolean itf;
	
	public ASMSequenceMethodVisitor(int api) {
		super(api);
		// TODO Auto-generated constructor stub
	}
	
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		this.name = name;
		types = new ArrayList<String>();
		for(Type t : Type.getArgumentTypes(desc)) {
			types.add(t.getClassName());
		}
		ASMSignatureVisitor ownerVisitor = new ASMSignatureVisitor(Opcodes.ASM7);
		SignatureReader ownerReader = new SignatureReader(owner);
		try {
			ownerReader.accept(ownerVisitor);
			for (TypeData t : ownerVisitor.getTypes()) {
				this.owner = t.name.replace('/', '.');
			}
		} catch (IllegalArgumentException e) {
			this.owner = owner.replace('/', '.');
		}
	}

	public String getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public List<String> getTypes() {
		return types;
	}

	public boolean isItf() {
		return itf;
	}

}
