#include "../include/compiler.h"

// parser
CompUnitNode *nodesRoot;

// 开始进行语法分析
void analyzeSyntax() { nodesRoot = parseCompUnit(); }

Node newNode(NodeType NodeType, Node *parent, Token *token) {
    Node node;
    node.nodeType = NodeType;
    node.parent = parent;
    node.token = token;
}

CompUnitNode *parseCompUnit() {
    CompUnitNode *node;
    node->node = newNode(CompUnit, NULL, NULL);
    while (hasNextToken()) {
        if (peekToken(0)->type == CONSTTK) {
            // constDecl
        } else if (peekToken(1)->type == MAINTK) {
            // mainFuncDef
        } else if (peekToken(2)->type == LPARENT) {
            // funcDef
        } else {
            // decl
        }
    }
    return node;
}
