$epicFeatureLinks = @(
  @("I_kwDOM95Igs8AAAABOzI5VQ", "I_kwDOM95Igs8AAAABOzJTHA"),  # Epic1 → F-01
  @("I_kwDOM95Igs8AAAABOzI52g", "I_kwDOM95Igs8AAAABOzJTvA"),  # Epic2 → F-02
  @("I_kwDOM95Igs8AAAABOzI6Tw", "I_kwDOM95Igs8AAAABOzJUGg"),  # Epic3 → F-03
  @("I_kwDOM95Igs8AAAABOzJABQ", "I_kwDOM95Igs8AAAABOzJUiA"),  # Epic4 → F-04
  @("I_kwDOM95Igs8AAAABOzJAZA", "I_kwDOM95Igs8AAAABOzJa8w"),  # Epic5 → F-05
  @("I_kwDOM95Igs8AAAABOzJA2w", "I_kwDOM95Igs8AAAABOzJbfw"),  # Epic6 → F-06
  @("I_kwDOM95Igs8AAAABOzJBLg", "I_kwDOM95Igs8AAAABOzJb8w"),  # Epic7 → F-07
  @("I_kwDOM95Igs8AAAABOzJJGg", "I_kwDOM95Igs8AAAABOzJcaQ"),  # Epic8 → F-08
  @("I_kwDOM95Igs8AAAABOzJJGg", "I_kwDOM95Igs8AAAABOzJc7A"),  # Epic8 → F-09
  @("I_kwDOM95Igs8AAAABOzJJiw", "I_kwDOM95Igs8AAAABOzJdaQ"),  # Epic9 → F-10
  @("I_kwDOM95Igs8AAAABOzJKFQ", "I_kwDOM95Igs8AAAABOzJimg"),  # Epic10 → F-11
  @("I_kwDOM95Igs8AAAABOzJKhw", "I_kwDOM95Igs8AAAABOzJjAg"),  # Epic11 → F-12
  @("I_kwDOM95Igs8AAAABOzJK6A", "I_kwDOM95Igs8AAAABOzJjfg"),  # Epic12 → F-13
  @("I_kwDOM95Igs8AAAABOzJK6A", "I_kwDOM95Igs8AAAABOzJj_Q"),  # Epic12 → F-14
  @("I_kwDOM95Igs8AAAABOzJLYA", "I_kwDOM95Igs8AAAABOzJkdA")   # Epic13 → F-15
)

$featureUsLinks = @(
  @("I_kwDOM95Igs8AAAABOzJTHA", "I_kwDOM95Igs8AAAABOzKJkg"),  # F-01 → US 1.1
  @("I_kwDOM95Igs8AAAABOzJTHA", "I_kwDOM95Igs8AAAABOzKKCQ"),  # F-01 → US 1.2
  @("I_kwDOM95Igs8AAAABOzJTHA", "I_kwDOM95Igs8AAAABOzKKlA"),  # F-01 → US 1.3
  @("I_kwDOM95Igs8AAAABOzJTvA", "I_kwDOM95Igs8AAAABOzKLFA"),  # F-02 → US 2.1
  @("I_kwDOM95Igs8AAAABOzJUGg", "I_kwDOM95Igs8AAAABOzKXzg"),  # F-03 → US 3.1
  @("I_kwDOM95Igs8AAAABOzJUGg", "I_kwDOM95Igs8AAAABOzKYWg"),  # F-03 → US 3.2
  @("I_kwDOM95Igs8AAAABOzJUiA", "I_kwDOM95Igs8AAAABOzKYxg"),  # F-04 → US 4.1
  @("I_kwDOM95Igs8AAAABOzJUiA", "I_kwDOM95Igs8AAAABOzKZPg"),  # F-04 → US 4.2
  @("I_kwDOM95Igs8AAAABOzJa8w", "I_kwDOM95Igs8AAAABOzKjEA"),  # F-05 → US 5.1
  @("I_kwDOM95Igs8AAAABOzJa8w", "I_kwDOM95Igs8AAAABOzKjiQ"),  # F-05 → US 5.2
  @("I_kwDOM95Igs8AAAABOzJbfw", "I_kwDOM95Igs8AAAABOzKj6Q"),  # F-06 → US 6.1
  @("I_kwDOM95Igs8AAAABOzJbfw", "I_kwDOM95Igs8AAAABOzKkUg"),  # F-06 → US 6.2
  @("I_kwDOM95Igs8AAAABOzJb8w", "I_kwDOM95Igs8AAAABOzKkxQ"),  # F-07 → US 7.1
  @("I_kwDOM95Igs8AAAABOzJb8w", "I_kwDOM95Igs8AAAABOzKlKw"),  # F-07 → US 7.2
  @("I_kwDOM95Igs8AAAABOzJb8w", "I_kwDOM95Igs8AAAABOzKluQ")   # F-07 → US 7.3
)

foreach ($link in $epicFeatureLinks) {
    $mutation = "mutation { addSubIssue(input: {issueId: `"$($link[0])`", subIssueId: `"$($link[1])`"}) { clientMutationId } }"
    gh api graphql -f query="$mutation" --jq ".data.addSubIssue.clientMutationId"
    Write-Host "Linked: $($link[0]) -> $($link[1])"
}

foreach ($link in $featureUsLinks) {
    $mutation = "mutation { addSubIssue(input: {issueId: `"$($link[0])`", subIssueId: `"$($link[1])`"}) { clientMutationId } }"
    gh api graphql -f query="$mutation" --jq ".data.addSubIssue.clientMutationId"
    Write-Host "Linked: $($link[0]) -> $($link[1])"
}

Write-Host "All relationships linked successfully."