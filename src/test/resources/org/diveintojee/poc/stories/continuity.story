Elasticsearch continuity stories

Meta:
@refs 1
@progress wip

Narrative:
Given I write to the search engine
When the search engine goes for maintainance
Then there should we no data loss, nor service interruption ie continuity

Scenario: search classified by title should succeed
When I search for classifieds which "title" matches "awesome"
Then I should get the following classifieds:
| title                        | description                |
| awesome title 20             | fflflflflflflflflflflfl    |
| awesome title 21             | awesome description 21     |

