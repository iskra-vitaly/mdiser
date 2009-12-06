function [tm, D] = benchIDiff(workDir, Itra)
  tm = cputime();
  D = calcDiffs(workDir, Itra);
  tm = cputime()-tm;
endfunction