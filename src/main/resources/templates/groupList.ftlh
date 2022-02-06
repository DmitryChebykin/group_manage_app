<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Студенческие группы</title>
    <style>
        th, td {
            border: solid black 1px;
            padding: 5px 5px;
            text-align: center;
        }

        span.pageNo {
            color: red;
            border: 1px solid red;
            display: inline-block;
            margin-bottom: 2px;
            margin-right: 5px;
            padding-right: 2px;
            padding-left: 2px;
        }
    </style>
</head>
<body>

<h2>Список групп</h2>

<table>
    <thead>
    <tr>
        <th>Группа</th>
        <th>Количество студентов</th>
        <th>Действие</th>

    </tr>
    </thead>

    <#list groups as group>
        <tr>
            <td>${group.groupNumber}</td>
            <td>${group.studentsQuantity}</td>
            <td><a href="groups/${group.groupNumber}">Edit</a></td>
        </tr>
    </#list>

</table>
<br/>
<div>Всего страниц <span class="pageNo">${total_page}</span></div>
<br/>
<div>Текущая страница <span class="pageNo">${pageNo}</span></div>
<br/>
<div>
    <form method="get" action="/groups">
        <label>Номер страницы
            <input type="number" name="page" min="0" max="${lastPageNo}" placeholder="Номер страницы" value="0">
        </label>
        <button type="submit">Перейти на страницу</button>
        <p>Нумерация страниц с <span class="pageNo">0</span>, последняя страница с данными - <span
                    class="pageNo">${lastPageNo}</span>)</p>
    </form>
</div>
<br/>
<div>
    <form method="post" action="/groups">
        <input type="text" name="groupNumber" pattern=".{1,}" required title="1 characters minimum"
               placeholder="Номер группы" modelAttribute="groupFormDto">
        <button type="submit">Добавить группу</button>
    </form>
</div>


<#if message??>
    <p>${message} </p>
</#if>

<#if messageLink??>
    <a href="/${messageLink}">Перейти</a>
</#if>

</body>
</html>