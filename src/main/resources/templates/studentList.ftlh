<!DOCTYPE html>
<html lang="ru">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <#if groupNumber??>
        <title>Группа ${groupNumber}</title>
    </#if>
    <style>
        th, td {
            border: solid black 1px;
            padding: 5px 5px;
            text-align: center;
        }
    </style>
</head>
<body>
<#if groupNumber??>
    <h1 data-group=${groupNumber}>Группа ${groupNumber}</h1>
    <h2>Список студентов</h2>

    <table>
        <thead>
        <tr>
            <th>Дата добавления</th>
            <th>Полное имя</th>
            <th>Действие (перезагрузка страницы)</th>
            <th>Действие (без перезагрузки)</th>
        </tr>
        </thead>
        <tbody class="body">

        <#list students as student>
            <tr>
                <td>${student.createdAt}</td>
                <td>${student.fullName}</td>
                <td><a href="/student/${student.fullName}">Delete</a></td>
                <td>
                    <button class=deleteStudent data-name="${student.fullName}" data-group="${groupNumber}"
                            onclick="deleteStudent('/student?', replaceTbodyContent, this)">Delete
                    </button>
                </td>
            </tr>
        </#list>

        </tbody>

    </table>
    <br>
    <div style="display: flex;
         justify-content: left">
        <div style="margin-right: 10px;">
            <form method="get" action="/groups">
                <button type="submit">Вернутся к списку групп</button>
            </form>
        </div>
        <div>
            <button><a style="text-decoration: none" href="/">На главную</a></button>
        </div>
    </div>

    <br>
    <div>
        <form class=addStudent method="post" action="/student" modelAttribute="studentFormDto">
            <input data-group="${groupNumber}" id="fullName" type="text" pattern=".{1,}" required
                   title="1 characters minimum"
                   name="fullName"
                   placeholder="Полное имя студента">
            <input type="hidden" name="groupNumber" value="${groupNumber}">
            <button type="submit">Добавить студента в группу (перезагрузка страницы)</button>
        </form>
        <br>
        <button class="btn" type="submit"
                onclick="addStudent('/add-student', replaceTbodyContent)">Добавить студента в группу (без перезагрузки
            страницы))
        </button>
    </div>

    <#if message??>
        <p>${message} </p>
    </#if>

<#else>
    <#if message??>
        <p>${message} </p>
    </#if>
    <div>
        <form method="get" action="/groups">
            <button type="submit">Вернутся к списку групп</button>
        </form>
    </div>
</#if>
<script type="text/javascript">

    function deleteStudent(url, cFunction, e) {
        const group = e.dataset.group;
        const name = e.dataset.name;

        let xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                cFunction(this);
            }
        };
        xhttp.open("GET", url + 'group=' + group + '&' + 'name=' + name, true);
        xhttp.send();
    }


    function addStudent(url, cFunction) {
        const name = document.getElementById('fullName').value
        const group = document.getElementById('fullName').dataset.group;

        let xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                cFunction(this);
            }
        };
        xhttp.open('POST', url, true);
        let param = 'groupNumber=' + group + '&fullName=' + name;
        xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhttp.send(param);
    }

    function replaceTbodyContent(xhttp) {
        document.getElementsByTagName('tbody').item(0).innerHTML = xhttp.responseText;
    }
</script>
</body>
</html>