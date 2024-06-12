# PSOによるパターンフォーメーション問題を扱う研究のプログラム
## 概要
群知能アルゴリズムの一つである粒子群最適化（PSO）を応用し、マルチエージェントパターンフォーメーション問題を解く手法を提案。\
その実験を行うプログラム群。\
baseline（既存手法）についてはhttps://ieeexplore.ieee.org/abstract/document/5445863/ を参照。\

以下では主に最新のfor_KES2024フォルダの内容と扱い方について触れる。\

## for_KES2024 内の構成（一部）
├─1\
│  ├─csv\
│  ├─csv_avoid\
│  ├─csv_proposal\
│  ├─csv_test\
│  ├─percent\
│  ├─percent_avoid\
│  ├─percent_origin\
│  ├─percent_proposal\
│  ├─pics\
│  ├─pics_avoid\
│  ├─pics_proposal\
│  └─pics_test\
├─environment\
│  ├─Agent.java\
│  ├─AgentDup.java\
│  ├─Agent_avoid.java\
│  ├─Agent_proposal.java\
│  ├─Grid.java\
│  ├─GridDup.java\
│  ├─Grid_avoid.java\
│  ├─Grid_proposal.java\
│  └─Variable.java\
├─PF_PSO_origin.java\
├─PF_PSO_avoid.java\
├─PF_PSO_test.java\
├─PF_PSO_proposal.java\
└─Position.java

## コンパイル・実行手順
for_KES2024直下で\
```
javac ./Position.java
java Position
javac ./PF_PSO_proposal.java ./environment/Variable.java ./environment/Agent_proposal.java ./environment/Grid_proposal.java
java PF_PSO_proposal
```
他のファイルも、以下の各項目の箇条書きの番号にならう形で対応付けられている。

## 検証用プログラム
1. PF_PSO_origin.java：baselineの実験検証のためのプログラム。
2. PF_PSO_avoid.java：提案手法1のプログラム。移動先での重複回避のみを行う。
3. PF_PSO_test.java：提案手法2のプログラム。パターン上に到達したエージェントが、一定時間ごとにランダムに動く。
4. PF_PSO_proposal.java（Fixedや2024_forKESで追加）：提案手法3のプログラム。パターン上に到達したエージェントが、隣接地点にフェロモンを分泌。また、探索が進むとエリアの分割を少なくする。

## Agent定義
1. AgentDup.java：baselineのAgent定義。Dupは重複を許すことから命名。
2. Agent_avoid.java：提案手法1のAgent定義。
3. Agent.java：提案手法2のAgent定義。
4. Agent_proposal.java（Fixedや2024_forKESで追加）：提案手法3のAgent定義。

## 与えるパターンについて
1. GridDup.java：baselineでの与えるパターンとそれを保持するマップ。
2. Grid_avoid.java：提案手法1での与えるパターンとマップ。
3. Grid.java：提案手法2での与えるパターンとマップ。
4. Grid_proposal.java（Fixedや2024_forKESで追加）：提案手法3での与えるパターンとマップ。

## パラメータ群や環境サイズ等の数値決定
Variable.java：ここで各パラメータを定義。\
Position.java：初期位置を決定。実験の前に実行しておく（initial_pos〇.csvの形で出力）

## データ整理
実験結果はcsv形式で、各エージェント毎にその位置が記録される（/csv/step1.csv　など）。また、充填率（パターン全体に対するエージェントの占有率）も記録される（/percent/percent.csv　など）。
CustomGetGraphRenewal.py：実験時に用いたマップと同じものを設定する手間はあるが、実験結果を可視化する。
