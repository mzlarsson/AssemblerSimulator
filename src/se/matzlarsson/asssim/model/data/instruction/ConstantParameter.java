package se.matzlarsson.asssim.model.data.instruction;

import se.matzlarsson.asssim.model.data.AssByte;
import se.matzlarsson.asssim.model.data.Machine;
import se.matzlarsson.asssim.model.data.SyntaxUtil;

public class ConstantParameter extends BasicParameter{
	
	private int byteSize;
	private boolean ignoreConstantPrefix;

	public ConstantParameter(String storeName, int byteSize, boolean ignoreConstantPrefix){
		super(storeName);
		this.byteSize = byteSize;
		this.ignoreConstantPrefix = ignoreConstantPrefix;
	}

	@Override
	public boolean isValid(Machine m, String input) {
		return (SyntaxUtil.isConstant(m.getSyntax(), input) || ignoreConstantPrefix) &&
			   SyntaxUtil.getNumericalType(m.getSyntax(), input)!=null &&
			   SyntaxUtil.getIntValue(m.getSyntax(), input)<Math.pow(256, getSize());
	}
	
	@Override
	public int getSize(){
		return this.byteSize;
	}

	@Override
	public AssByte[] prepareBytes(Machine m, String input) {
		String concreteValue = SyntaxUtil.getConcreteValue(m.getSyntax(), input);
		return AssByte.getAssBytes(SyntaxUtil.getNumericalType(m.getSyntax(), input), concreteValue);
	}

	@Override
	public void perform(Machine m, String input) {
		AssByte[] data = AssByte.getAssBytes(SyntaxUtil.getNumericalType(m.getSyntax(), input),
											 SyntaxUtil.getRealValue(m.getSyntax(), input));
		
		if(data.length==this.byteSize){
			super.perform(m, data);
		}else{
			AssByte[] b = new AssByte[this.byteSize];
			int i = 0;
			for(; i<this.byteSize && i<data.length; i++){
				b[this.byteSize-1-i] = data[data.length-1-i];
			}
			for(; i<this.byteSize; i++){
				b[this.byteSize-1-i] = new AssByte();
			}
			
			super.perform(m, b);
		}
	}
	
}
