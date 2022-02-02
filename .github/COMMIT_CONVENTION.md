## Git Commit Message Convention
> https://pepega.tistory.com/38 - Explains conventional Commit(Korean)
> https://github.com/discordjs/discord.js/blob/main/.github/COMMIT_CONVENTION.md - Example Convention(English)

### 간단요약
메시지는 해당 형식대로 작성되어야 합니다.
```js
/^(revert: )?(feat|fix|docs|style|refactor|perf|test|workflow|build|ci|chore|types|wip)(\(.+\))?: .{1,72}/;
```

1. feat(Features) 헤더 - 새로운 기능 추가시 사용. subheader에 기능이 추가된 범주 작성
```
feat(login): add response '[POST] login/auth'
```
2. fix(Bug Fixes) 헤더 - 오류 수정 시 사용. github Issue 번호와 함께 링크할것
```
fix(MemberRepository): fix Bugs that repo can't connect to db after 100 times approach

close #20
```
3. perf(Performance Improvements) 헤더 - 기능 최적화 시 사용. BREAKING CHANGE를 통해 중요한 변경 부분 설명
```
perf(MemberService): improve fetch from DB by removing useless db approach

BREAKING CHANGE: fetchMemberDatabase()에서의 EnroleRepository 참조가 제거됨
```

### 전체 메시지 포맷(Full Message Format)
커밋 메시지는 header, body, footer로 이루어지며, 헤더는 type, scope, subject로 이루어져 있습니다.
```
<type>(<scope>): <subject>
//blank
<body>
//blank
<footer>
```

##### Revert
만약 해당 커밋이 revert commit인 경우, `revert: `로 시작하는 커밋 메시지를 작성하십시오. 이 경우, body에서 반드시 
`This reverts commit <hash>`를 통해 해당 커밋이 어느 커밋으로 revert했는지 표기해야 합니다.
##### Type
만약 수정사항이 `feat`, `fix`, `perf`중 하나인 경우, 해당 커밋을 관리자는 changelog에 기록해야 합니다. 만약, 중요한
수정 사항이 존재한다면, 마찬가지로 changelog에 기록하시길 바랍니다.

다른 중요하지 않은 수정 사항들은 임의적으로 사용하시기 바랍니다. 사용을 권장하는 태그는 `docs`, `chore`, `style`, 
`refactor`, `test`입니다. 해당 수정사항들은 changelog에 기록되지 않습니다.
##### Scope
Scope는 해당 수정사항이 이루어진 곳을 가리킵니다. 변경된 클래스 이름 등을 작성하시기 바랍니다.
##### Subject
Subject는 수정사항에 대한 간단한 설명입니다:
* 가능한 한 명령형, 현재 시제로 작성하시기 바랍니다(ex. 변경하였다, 변경됨 대신 변경 사용).
* (영어일 경우) 첫 글자는 대문자일 필요가 없습니다.
* 마지막에 마침표(.)는 사용하지 않습니다.
##### Body
Body는 해당 변경의 이유, 변경 전과의 차이점 등을 담고 있어야 하며, Subject와 마찬가지 규칙으로 작성하시기 바랍니다.
##### Footer
Footer는 Breaking Changes 또는 해당 fix를 통해 닫힐 Github issue id 등 특수한 추가 정보를 작성합니다.