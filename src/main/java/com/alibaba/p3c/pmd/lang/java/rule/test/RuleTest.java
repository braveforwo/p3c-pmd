package com.alibaba.p3c.pmd.lang.java.rule.test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.documentation.AbstractCommentRule;

import java.util.*;


public class RuleTest extends AbstractCommentRule {
    @Override
    public Object visit(final ASTClassOrInterfaceBody node, Object data) {
        ASTMethodDeclaration preMethodDeclaration =null;
        for(int i =0;i<node.jjtGetNumChildren();i++){
            if(node.jjtGetChild(i).hasDescendantOfType(ASTMethodDeclaration.class)){
                for(int j =0;j<node.jjtGetChild(i).jjtGetNumChildren();j++){
                    if(node.jjtGetChild(i).jjtGetChild(j) instanceof ASTMethodDeclaration){
                        if(preMethodDeclaration==null){
                            preMethodDeclaration=(ASTMethodDeclaration)node.jjtGetChild(i).jjtGetChild(j);
                            continue;
                        }
                        if(node.jjtGetChild(i).jjtGetChild(j).getBeginLine()-preMethodDeclaration.getEndLine()-1!=1){
                            addViolationWithMessage(data,node.jjtGetChild(i).jjtGetChild(j),"每个方法之间间隔应为一行");
                            preMethodDeclaration=(ASTMethodDeclaration)node.jjtGetChild(i).jjtGetChild(j);
                        }else{
                            preMethodDeclaration=(ASTMethodDeclaration)node.jjtGetChild(i).jjtGetChild(j);
                        }
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        assignCommentsToDeclarations(cUnit);
        return super.visit(cUnit, data);
    }

    @Override
    protected SortedMap<Integer, Node> orderedCommentsAndDeclarations(ASTCompilationUnit cUnit) {
        SortedMap<Integer, Node> itemsByLineNumber = new TreeMap<>();
        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTAnnotation.class, true));
        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.getComments());

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTFieldDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTMethodDeclaration.class, true));
        addDeclarations(itemsByLineNumber,cUnit.findDescendantsOfType(ASTBlockStatement.class,true));
        addDeclarations(itemsByLineNumber,cUnit.findDescendantsOfType(ASTBlock.class,true));
        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTConstructorDeclaration.class, true));

        addDeclarations(itemsByLineNumber, cUnit.findDescendantsOfType(ASTEnumDeclaration.class, true));

        return itemsByLineNumber;
    }

    private void addDeclarations(SortedMap<Integer, Node> map, List<? extends Node> nodes) {
        for (Node node : nodes) {
            map.put((node.getBeginLine() << 16) + node.getBeginColumn(), node);
        }
    }

    protected void assignCommentsToDeclarations(ASTCompilationUnit cUnit) {

        SortedMap<Integer, Node> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);
        Node beginOfMethod = null;
        ASTMethodDeclaration preAstMethodDeclaration = null;
        for (Map.Entry<Integer, Node> entry : itemsByLineNumber.entrySet()) {
            if(entry.getValue() instanceof SingleLineComment || entry.getValue() instanceof MultiLineComment || entry.getValue() instanceof FormalComment || entry.getValue() instanceof ASTAnnotation){
                 if (beginOfMethod==null&&(preAstMethodDeclaration==null||preAstMethodDeclaration.getEndLine()<entry.getValue().getBeginLine())){
                     beginOfMethod = entry.getValue();
                 }
            }else if(entry.getValue() instanceof ASTMethodDeclaration){
                if (beginOfMethod!=null) {
                    ASTMethodDeclaration astMethodDeclaration =(ASTMethodDeclaration) entry.getValue();
                    preAstMethodDeclaration = astMethodDeclaration;
                    astMethodDeclaration.testingOnlySetBeginLine(beginOfMethod.getBeginLine());
                    beginOfMethod=null;
                }
            }else{
                 beginOfMethod=null;
                 continue;
            }
        }
    }

    protected List<Node> mapTurnToList(SortedMap<Integer, Node> items){
        List<Node> nodes =new ArrayList<Node>();
        for (Map.Entry<Integer, Node> entry : items.entrySet()){
                nodes.add(entry.getValue());
        }
        return nodes;
    }
}