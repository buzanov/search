# Инфопоиск
Домашние задания по инфопоиску 

## Задание 1: 

task1 
<br /> 
&#8595;
<br /> src <br />
&#8595;
<br /> main 
&#8594; kotlin &#8594; Main.kt - код решения <br />
&#8595; <br />
resources &#8594; index.txt (Файл &#8594; сайт), list.txt (Список сайтов)
<br /> &#8595; <br /> выкачка - директория с файлами и данными

## Задание 2:

Леммы и токены из всех файлов выкачки были объединены в один файл для лемм и один файл для токенов.

task2
<br />
&#8595;
<br /> src <br />
&#8595;
<br /> main
&#8594; kotlin &#8594; Main.kt - код решения <br />
&#8595; <br />
resources &#8594; lemmas.txt (Леммы), tokens.txt (Токены)

## Задание 3:

Индекс файл лежит в task3/src/main/resources

Код для построения индекса лежит в task3/src/main/kotlin/IndexBuilder.kt

Код для чтения индекса и булева поиска находится в task3/src/main/kotlin/IndexBuilder.kt

Для лемматизации токенов использвался jar файл из task2, если потребуется запустить самостоятельно, 
то нужно после загрузки репозитория нужно прописать gradle build в директории task3.