## analyze bootstap images

function bootstrap(dbDir, matDir, output, m, w, h, nb)
  bTotal = NaN;
  nTotal = NaN;
  B = zeros(nb, w*h, m);
  for k=1:m
    subjectName = sprintf("%02d", k);
    subjectDir = sprintf("%s/%s/%s", dbDir, subjectName, matDir);
    #fprintf("Processing %s...\n", subjectDir);
    normMat = readNorm(subjectDir);
    bMat = calcBasis(normMat);
    normV = normMat(:,:);
    bV = bMat(:,:);
    if isnan(bTotal)
      bTotal = bV;
    else
      bTotal = [bTotal bV];
    endif
    if isnan(nTotal)
      nTotal = normV;
    else
      nTotal = [nTotal normV];
    endif
    B(:, :, k) = bV;
  endfor
  bMean = zeros(nb, w*h);
  bMean = mean(B, 3);
  bCov = zeros(nb, nb, w*h);
  for x=1:w*h
    observations = zeros(nb, m);
    observations(:,:) = B(:,x,:);
    bCov(:,:,x) = cov(observations'); # covariance of B for x'th pixel
  endfor
  save(output, "bMean", "bCov");
  lights = generateLights(2);
  condCnt = rows(lights);
  alpha = cell(condCnt, 1);
  for i=1:condCnt    # IMG = B'*aplha+E
    IMG = applyLightCond(nTotal, lights{i, 1}, lights{i, 2});
    [ALPHA, SIGMA, E] = ols(IMG, B');
    alpha()
  endfor
end

function showVec(V, w, h)
  VV = zeros(h, w);
  VV(:) = V(:);
  imshow(VV);
end