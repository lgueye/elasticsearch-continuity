Elasticsearch continuity stories

Meta:
@refs 1
@progress wip

Narrative:
Given I ask for documents indexing
When the search engine goes for maintainance
Then there should be no data loss, nor service interruption (ie continuity)

Scenario: search classified by title should succeed
Given I create the following classifieds:
| title           | description         |
| awesome title 1 | great description 1 |
| awesome title 2 | great description 2 |
| great title     | awesome description |
When I search for classifieds which "title" matches "awesome"
Then I should get the following classifieds:
| title           | description         |
| awesome title 2 | great description 2 |
| awesome title 1 | great description 1 |
When I create a valid classified:
| title           | description          |
| awesome title 3 | whatever description |
When I search for classifieds which "title" matches "awesome"
Then I should get the following classifieds:
| title           | description          |
| awesome title 3 | whatever description |
| awesome title 2 | great description 2  |
| awesome title 1 | great description 1  |
When the system stops consuming messages
And I create a valid classified:
| title          | description          |
| whatever title | whatever description |
When I search for classifieds which "title" matches "whatever"
Then I should get no results
When I trigger a reindex operation
And I start consuming messages
And I search for classifieds which "title" matches "whatever"
Then I should get the following classifieds:
| title           | description          |
| whatever title  | whatever description |
