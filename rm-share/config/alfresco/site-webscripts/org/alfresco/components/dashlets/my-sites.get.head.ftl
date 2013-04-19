<#-- We need this file so that RM works also with 4.1.3. This file can be deleted after the dependency has been updated to 4.1.4 -->

<#include "../component.head.inc">
<!-- My Sites -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/dashlets/my-sites.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/dashlets/my-sites.js"></@script>
<!-- Delete Site -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/delete-site.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/delete-site.js"></@script>