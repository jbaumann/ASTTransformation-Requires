import static org.objectweb.asm.Opcodes.*

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class RequiresTransformation implements ASTTransformation {
	
	def annotationType = Requires.class.name
	
	private boolean checkNode(astNodes, annotationType) {
		if (! astNodes) return false
		if (! astNodes[0]) return false
		if (! astNodes[1]) return false
		if (!(astNodes[0] instanceof AnnotationNode)) return false
		if (! astNodes[0].classNode?.name == annotationType) return false
		if (!(astNodes[1] instanceof MethodNode)) return false
		true
	}
   
	public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {

		if (!checkNode(astNodes, annotationType)) {
			// add an error message or a warning
			return
		}

		MethodNode annotatedMethod = astNodes[1]
		def annotationExpression = astNodes[0].members.value
		
		if (annotationExpression.class != ConstantExpression) {
			// add better error handling
			return
		}

		String annotationValueString = annotationExpression.value
		BlockStatement block = createStatements(annotationValueString)
		
		def methodStatements = annotatedMethod.code.statements
		methodStatements.add(0, block)
	}
	
	BlockStatement createStatements(String clause) {
		def statements = """
			if(!($clause)) { 
				throw new Exception('Precondition violated: {$clause}')
			}
		"""
		AstBuilder ab = new AstBuilder()
		List<ASTNode> res = ab.buildFromString(CompilePhase.SEMANTIC_ANALYSIS, false, statements)
		BlockStatement bs = res[0]
		return bs
	}
}
