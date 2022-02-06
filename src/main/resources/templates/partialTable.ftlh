<#list students as student>
    <tr>
        <td>${student.createdAt}</td>
        <td>${student.fullName}</td>
        <td><a href="/student/${student.fullName}">Delete</a></td>
        <td>
            <button class=deleteStudent data-name="${student.fullName}" data-group="${groupNumber}"
                    onclick="return deleteStudent('/student?', replaceTbodyContent, this)">Delete
            </button>
        </td>
    </tr>
</#list>
<#if message??>
    <p>${message} </p>
</#if>
