cd %~dp0
gcc -Wall -g -o .\testcase\compiler.exe .\src\main.c .\src\lexer.c .\src\parser.c .\src\pcode.c .\src\tool.c .\src\error.c