$mutation = 'mutation($did:ID!,$b:String){updateProjectV2DraftIssue(input:{draftIssueId:$did body:$b}){draftIssue{title}}}'

$updates = @(
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhrk"; f="epic1"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhrs"; f="epic2"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhr0"; f="epic3"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhsA"; f="epic4"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhsE"; f="epic5"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhsQ"; f="epic6"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhsY"; f="epic7"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhsk"; f="epic8"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhso"; f="epic9"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhs0"; f="epic10"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhtE"; f="epic11"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhto"; f="epic12"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhtw"; f="epic13"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhww"; f="f01"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhw4"; f="f02"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhxA"; f="f03"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhxQ"; f="f04"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhxY"; f="f05"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhxo"; f="f06"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhxw"; f="f07"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhyA"; f="f08"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhyI"; f="f09"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhyY"; f="f10"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhyk"; f="f11"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhys"; f="f12"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhzA"; f="f13"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhzI"; f="f14"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jhzQ"; f="f15"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh1o"; f="us11"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh14"; f="us12"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh2E"; f="us13"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh2c"; f="us21"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh2o"; f="us31"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh20"; f="us32"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh28"; f="us41"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh3Y"; f="us42"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh3g"; f="us51"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh3o"; f="us52"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh4A"; f="us61"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh4Y"; f="us62"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh4k"; f="us71"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh4o"; f="us72"}
  ,@{id="PVTI_lAHOBn6SPs4BhJgizg4jh4w"; f="us73"}
)

$bodyDir = "$env:TEMP\card_bodies"
New-Item -ItemType Directory -Path $bodyDir -Force | Out-Null

foreach ($u in $updates) {
  $bodyFile = Join-Path $bodyDir "$($u.f).md"
  $body = Get-Content $bodyFile -Raw
  gh api graphql -f query="$mutation" -F did="$($u.id)" -F b="$body" --jq ".data.updateProjectV2DraftIssue.draftIssue.title" 2>&1 | Out-Null
  Write-Host "Updated: $($u.f)"
}

Write-Host "All done."