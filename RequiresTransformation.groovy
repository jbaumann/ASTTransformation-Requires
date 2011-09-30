import java.util.Arrays

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.Message
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
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
			//From LogASTTransformation
			addError("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(astNodes), astNodes[0], sourceUnit);
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
	
	public void addError(String msg, ASTNode node, SourceUnit source) {
		//From LogASTTransformation
		int line = node.getLineNumber()
		int col = node.getColumnNumber()
		SyntaxException se = new SyntaxException(msg + '\n', line, col)
		Message message = new SyntaxErrorMessage(se, source)
		source.getErrorCollector().addErrorAndContinue(message);
	}

}
