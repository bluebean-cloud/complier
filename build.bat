cd %~dp0
gcc -Wall -o compiler.exe .\src\main.c .\src\lexer.c .\src\parser.c .\src\vector.c