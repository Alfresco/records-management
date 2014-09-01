function main()
{
   model.recordableVersions = [];
   var result = remote.call("/slingshot/doclib/action/recorded-version-config/node/" + args.nodeRef);
   if (result.status == 200)
   {
      model.recordableVersions = eval('(' + result + ')').data.recordableVersions;
   }
}

main();