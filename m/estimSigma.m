## [sigmaS, sigmaC] = estimSigma(workDir, toleranceS, toleranceC, dim, progressFile)


function [sigmaS, sigmaC] = estimSigma(workDir, toleranceS, toleranceC, dim, progressFile)
  sigmaS = zeros(dim.subjects, dim.conditions);
  progress = struct("stage", 1, "done", "00%", "cond", 0);
  for cond=1:dim.conditions
    sigmaS(:, cond) = calcSigmaS(workDir, cond, toleranceS, dim);
    progress.cond = cond;
    progress.done = sprintf("%02.1f%%", double(cond*100/dim.conditions));
    save(progressFile, "progress");
  endfor

  dens = 1;
  sigmaC = zeros(dim.subjects, dim.conditions);
  progress = struct("stage", 2, "done", "00%", "subj", 0);
  for subj=1:dim.subjects
    if mod(subj-1, dens)==0
      sigmaC(subj, :) = calcSigmaC(workDir, subj, toleranceC, dim);
    else 
      sigmaC(subj, :) = sigmaS(subj-mod(subj-1, dens), :)
    endif
    progress.subj = subj;
    progress.done = sprintf("%02.1f%%", double(subj*100/dim.subjects));
    save(progressFile, "progress");
  endfor

  save("estimSigma.res", "sigmaS", "sigmaC");
endfunction