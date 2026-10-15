'use strict';

/**
 * backend/server.js
 * Hackathon AI Mentor – Express API Server
 *
 * CommonJS only.  No TypeScript, no ESM imports.
 * All paths are resolved relative to this file so the server works
 * regardless of the terminal's current working directory.
 */

const express = require('express');
const path    = require('path');

// ─── Port ────────────────────────────────────────────────────────────────────
const PORT = process.env.PORT || 3000;

// ─── Resolved paths ──────────────────────────────────────────────────────────
const FRONTEND_DIR  = path.resolve(__dirname, '..', 'frontend');
const MENTOR_PATH   = path.resolve(__dirname, '..', 'modules', 'mentor.js');
const PLANNER_PATH  = path.resolve(__dirname, '..', 'modules', 'planner.js');
const DEMO_PATH     = path.resolve(__dirname, '..', 'modules', 'demo.js');

// ─── App ─────────────────────────────────────────────────────────────────────
const app = express();
app.use(express.json());

// ─── Static frontend ─────────────────────────────────────────────────────────
app.use(express.static(FRONTEND_DIR));

// ─── GET /api/health ─────────────────────────────────────────────────────────
app.get('/api/health', function (req, res) {
  res.json({
    status  : 'ok',
    message : 'Hackathon AI Mentor server is running',
    time    : new Date().toISOString(),
  });
});

// ─── Input validation ────────────────────────────────────────────────────────
/**
 * Returns a string error message if the body is invalid, or null if valid.
 * A positive integer is defined as a finite integer > 0.
 */
function validateBody(body) {
  if (!body || typeof body !== 'object') {
    return 'Request body must be a JSON object.';
  }

  const { problem_statement, team_size, time_limit_minutes } = body;

  if (typeof problem_statement !== 'string' || problem_statement.trim() === '') {
    return '"problem_statement" must be a non-empty string.';
  }

  if (
    typeof team_size !== 'number' ||
    !Number.isInteger(team_size) ||
    team_size <= 0
  ) {
    return '"team_size" must be a positive integer.';
  }

  if (
    typeof time_limit_minutes !== 'number' ||
    !Number.isInteger(time_limit_minutes) ||
    time_limit_minutes <= 0
  ) {
    return '"time_limit_minutes" must be a positive integer.';
  }

  return null;
}

// ─── Response field validators ───────────────────────────────────────────────
/**
 * Returns an error string if the mentor object is missing required array fields.
 */
function validateMentor(obj) {
  const required = ['problem_summary', 'users', 'user_needs', 'features'];
  for (const key of required) {
    if (!(key in obj)) return 'mentor.' + key + ' field is missing.';
  }
  if (!Array.isArray(obj.users))       return 'mentor.users must be an array.';
  if (!Array.isArray(obj.user_needs))  return 'mentor.user_needs must be an array.';
  if (!Array.isArray(obj.features))    return 'mentor.features must be an array.';
  return null;
}

/**
 * Returns an error string if the plan object is missing required array fields.
 */
function validatePlan(obj) {
  const required = ['mvp_plan', 'tech_stack', 'tasks', 'validation_checkpoints'];
  for (const key of required) {
    if (!(key in obj)) return 'plan.' + key + ' field is missing.';
  }
  if (!Array.isArray(obj.mvp_plan))                return 'plan.mvp_plan must be an array.';
  if (!Array.isArray(obj.tech_stack))              return 'plan.tech_stack must be an array.';
  if (!Array.isArray(obj.tasks))                   return 'plan.tasks must be an array.';
  if (!Array.isArray(obj.validation_checkpoints))  return 'plan.validation_checkpoints must be an array.';
  return null;
}

/**
 * Returns an error string if the demo object is missing required array fields.
 */
function validateDemo(obj) {
  const required = ['test_cases', 'demo_checklist', 'jury_presentation_outline'];
  for (const key of required) {
    if (!(key in obj)) return 'demo.' + key + ' field is missing.';
  }
  if (!Array.isArray(obj.test_cases))                  return 'demo.test_cases must be an array.';
  if (!Array.isArray(obj.demo_checklist))              return 'demo.demo_checklist must be an array.';
  if (!Array.isArray(obj.jury_presentation_outline))   return 'demo.jury_presentation_outline must be an array.';
  return null;
}

// ─── Module loader ───────────────────────────────────────────────────────────
/**
 * Try to require a module file.  Returns { fn, error }.
 * If the file does not exist or exports nothing, fn will be null and error
 * will contain a description so the route can decide to fall back to the mock.
 */
function loadModule(absolutePath, exportedName) {
  try {
    // Use require.resolve to check existence before requiring, so we get a
    // clear error instead of a Node.js MODULE_NOT_FOUND stack trace in prod.
    require.resolve(absolutePath);
    const mod = require(absolutePath);
    if (typeof mod[exportedName] !== 'function') {
      return { fn: null, error: absolutePath + ' does not export a function named "' + exportedName + '".' };
    }
    return { fn: mod[exportedName], error: null };
  } catch (e) {
    return { fn: null, error: e.message };
  }
}

// ─── Check whether all three real modules are ready ──────────────────────────
/**
 * Attempt to load all three modules at startup and cache the result.
 * Re-evaluated on every request so a hot-swap (unlikely in prod but useful
 * in development) is picked up automatically.
 */
function resolveModules() {
  const mentor  = loadModule(MENTOR_PATH,  'generateMentor');
  const planner = loadModule(PLANNER_PATH, 'generatePlan');
  const demo    = loadModule(DEMO_PATH,    'generateDemo');

  const allReady =
    mentor.fn  !== null &&
    planner.fn !== null &&
    demo.fn    !== null;

  return { mentor, planner, demo, allReady };
}

// ─── TEMPORARY MOCK ADAPTER ──────────────────────────────────────────────────
// LABEL: MOCK – remove from the active request path once all three module
//        functions (generateMentor, generatePlan, generateDemo) are available.
//
// This adapter is ONLY used when one or more module files are empty / missing.
// It returns the exact response structure so the frontend can connect and be
// tested independently of the module authors.
// ─────────────────────────────────────────────────────────────────────────────
function mockGenerateMentor(input) {
  return {
    problem_summary : '[MOCK] ' + input.problem_statement,
    users           : ['[MOCK] Student', '[MOCK] Canteen manager'],
    user_needs      : ['[MOCK] Reduce leftovers', '[MOCK] Better demand forecast'],
    features        : ['[MOCK] AI demand prediction', '[MOCK] Real-time inventory dashboard'],
  };
}

function mockGeneratePlan(input) {
  return {
    mvp_plan               : ['[MOCK] Collect weekly menu data', '[MOCK] Build prediction model'],
    tech_stack             : ['[MOCK] Node.js', '[MOCK] Python Flask', '[MOCK] React'],
    tasks                  : [
      { assignee: 'Dev 1', task: '[MOCK] Data collection script' },
      { assignee: 'Dev 2', task: '[MOCK] Prediction API'        },
    ],
    validation_checkpoints : ['[MOCK] Model accuracy > 80%', '[MOCK] UI demo-ready at T-30min'],
  };
}

function mockGenerateDemo(input) {
  return {
    test_cases                : ['[MOCK] Submit known menu → verify prediction', '[MOCK] Empty input → graceful error'],
    demo_checklist            : ['[MOCK] Server running', '[MOCK] Sample data loaded', '[MOCK] Slide deck ready'],
    jury_presentation_outline : ['[MOCK] Problem (1 min)', '[MOCK] Solution demo (3 min)', '[MOCK] Impact (1 min)'],
  };
}
// ─── END MOCK ADAPTER ────────────────────────────────────────────────────────

// ─── POST /api/mentor ────────────────────────────────────────────────────────
app.post('/api/mentor', function (req, res) {
  // 1. Validate input
  const validationError = validateBody(req.body);
  if (validationError) {
    return res.status(400).json({ success: false, error: validationError });
  }

  const { problem_statement, team_size, time_limit_minutes } = req.body;

  const input = {
    problem_statement : problem_statement.trim(),
    team_size,
    time_limit_minutes,
  };

  // 2. Decide: use real modules or mock adapter
  const modules = resolveModules();

  if (!modules.allReady) {
    // Log which modules are not yet ready (server console only – not exposed to client)
    if (modules.mentor.error)  console.warn('[MOCK ACTIVE] mentor.js  :', modules.mentor.error);
    if (modules.planner.error) console.warn('[MOCK ACTIVE] planner.js :', modules.planner.error);
    if (modules.demo.error)    console.warn('[MOCK ACTIVE] demo.js    :', modules.demo.error);
  }

  try {
    // 3. Execute mentor → planner → demo in order
    let mentor, plan, demo;

    if (modules.allReady) {
      // ── REAL INTEGRATION PATH ──────────────────────────────────────────────
      mentor = modules.mentor.fn(input);

      const mentorError = validateMentor(mentor);
      if (mentorError) {
        console.error('[ERROR] generateMentor returned invalid object:', mentorError);
        return res.status(502).json({ success: false, error: 'Module error: ' + mentorError });
      }

      plan = modules.planner.fn(Object.assign({}, input, { mentor }));

      const planError = validatePlan(plan);
      if (planError) {
        console.error('[ERROR] generatePlan returned invalid object:', planError);
        return res.status(502).json({ success: false, error: 'Module error: ' + planError });
      }

      demo = modules.demo.fn({
        problem_statement : input.problem_statement,
        mentor,
        plan,
      });

      const demoError = validateDemo(demo);
      if (demoError) {
        console.error('[ERROR] generateDemo returned invalid object:', demoError);
        return res.status(502).json({ success: false, error: 'Module error: ' + demoError });
      }
    } else {
      // ── MOCK ADAPTER PATH (TEMPORARY) ──────────────────────────────────────
      mentor = mockGenerateMentor(input);
      plan   = mockGeneratePlan(Object.assign({}, input, { mentor }));
      demo   = mockGenerateDemo({ problem_statement: input.problem_statement, mentor, plan });
    }

    // 4. Assemble and return the combined response
    return res.status(200).json({
      success : true,
      _mock   : !modules.allReady, // Remove or set to false once real modules are wired
      input   : {
        problem_statement : input.problem_statement,
        team_size         : input.team_size,
        time_limit_minutes: input.time_limit_minutes,
      },
      mentor,
      plan,
      demo,
    });

  } catch (err) {
    // Catch any exception thrown by a module – do NOT expose stack traces
    console.error('[ERROR] Uncaught exception during module execution:', err);
    return res.status(500).json({
      success : false,
      error   : 'An internal server error occurred. Check server logs for details.',
    });
  }
});

// ─── Fallback: serve index.html for all non-API GET routes ───────────────────
app.get('*', function (req, res) {
  res.sendFile(path.join(FRONTEND_DIR, 'index.html'));
});

// ─── Start ────────────────────────────────────────────────────────────────────
app.listen(PORT, function () {
  const modules = resolveModules();
  console.log('');
  console.log('┌─────────────────────────────────────────────────────┐');
  console.log('│        Hackathon AI Mentor – Server Starting        │');
  console.log('├─────────────────────────────────────────────────────┤');
  console.log('│  URL  : http://localhost:' + PORT + '                        │');
  console.log('│  Mode : ' + (modules.allReady ? 'REAL modules active             ' : 'MOCK adapter active (modules empty)') + '  │');
  console.log('└─────────────────────────────────────────────────────┘');
  console.log('');
  if (!modules.allReady) {
    console.warn('  ⚠  One or more module files are empty or not yet implemented.');
    if (modules.mentor.error)  console.warn('     mentor.js  -', modules.mentor.error);
    if (modules.planner.error) console.warn('     planner.js -', modules.planner.error);
    if (modules.demo.error)    console.warn('     demo.js    -', modules.demo.error);
    console.warn('  ⚠  Server is running with the TEMPORARY MOCK ADAPTER.');
    console.warn('     Replace mock with real modules per the integration contract.');
    console.warn('');
  }
});
